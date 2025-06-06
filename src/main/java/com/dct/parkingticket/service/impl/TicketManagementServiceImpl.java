package com.dct.parkingticket.service.impl;

import com.dct.parkingticket.common.Common;
import com.dct.parkingticket.common.JsonUtils;
import com.dct.parkingticket.constants.Esp32Constants;
import com.dct.parkingticket.constants.ExceptionConstants;
import com.dct.parkingticket.dto.esp32.Message;
import com.dct.parkingticket.dto.esp32.TicketFilterRequestDTO;
import com.dct.parkingticket.dto.esp32.TicketScanLogFilterRequestDTO;
import com.dct.parkingticket.dto.mapping.ITicketDTO;
import com.dct.parkingticket.dto.mapping.ITicketScanLogDTO;
import com.dct.parkingticket.dto.mapping.TicketScanLogStatisticDTO;
import com.dct.parkingticket.dto.request.TicketScanLogStatisticRequestDTO;
import com.dct.parkingticket.dto.response.BaseResponseDTO;
import com.dct.parkingticket.entity.Ticket;
import com.dct.parkingticket.entity.TicketScanLog;
import com.dct.parkingticket.exception.BaseBadRequestException;
import com.dct.parkingticket.repositories.TicketRepository;
import com.dct.parkingticket.repositories.TicketScanLogRepository;
import com.dct.parkingticket.service.TicketManagementService;
import com.dct.parkingticket.service.mqtt.MqttProducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TicketManagementServiceImpl implements TicketManagementService {

    private static final Logger log = LoggerFactory.getLogger(TicketManagementServiceImpl.class);
    private static final String ENTITY_NAME = "TicketManagementServiceImpl";
    private final TicketRepository ticketRepository;
    private final TicketScanLogRepository ticketScanLogRepository;
    private final MqttProducer mqttProducer;

    public TicketManagementServiceImpl(TicketRepository ticketRepository,
                                       TicketScanLogRepository ticketScanLogRepository,
                                       MqttProducer mqttProducer) {
        this.ticketRepository = ticketRepository;
        this.ticketScanLogRepository = ticketScanLogRepository;
        this.mqttProducer = mqttProducer;
    }

    @Override
    public void scanTicket(String uid) {
        Optional<Ticket> ticketOptional = ticketRepository.findByUid(uid);
        Message message = new Message();

        if (ticketOptional.isEmpty()) {
            message.setAction(Esp32Constants.Action.READ_TICKET_NOT_FOUND);
            message.setMessage(Esp32Constants.Response.TICKET_NOT_FOUND);
            saveTicketScanLog(uid, Esp32Constants.Response.TICKET_NOT_FOUND);
            mqttProducer.sendToEsp32(JsonUtils.toJsonString(message));
            return;
        }

        Ticket ticket = ticketOptional.get();

        switch (ticket.getStatus()) {
            case Esp32Constants.TicketStatus.ACTIVE -> {
                message.setAction(Esp32Constants.Action.READ_TICKET_ACTIVE);
                message.setMessage(Esp32Constants.Response.TICKET_ACTIVE);
                saveTicketScanLog(uid, null);
            }

            case Esp32Constants.TicketStatus.LOCKED -> {
                message.setAction(Esp32Constants.Action.READ_TICKET_LOCKED);
                message.setMessage(Esp32Constants.Response.TICKET_LOCKED);
                saveTicketScanLog(uid, Esp32Constants.Response.TICKET_LOCKED);
            }

            default -> {
                message.setAction(Esp32Constants.Action.READ_TICKET_INVALID);
                message.setMessage(Esp32Constants.Response.TICKET_INVALID);
                saveTicketScanLog(uid, Esp32Constants.Response.TICKET_INVALID);
            }
        }

        mqttProducer.sendToEsp32(JsonUtils.toJsonString(message));
    }

    @Async
    protected void saveTicketScanLog(String uid, String error) {
        Optional<TicketScanLog> lastScanLogOpt = ticketScanLogRepository.findTopByUidAndResultOrderByScanTimeDesc(
            uid,
            Esp32Constants.TicketScanResult.VALID
        );

        TicketScanLog newLog = new TicketScanLog();
        String scanType = Esp32Constants.TicketScanType.CHECKIN;
        String scanResult = Esp32Constants.TicketScanResult.VALID;

        if (StringUtils.hasText(error)) {
            newLog.setMessage(error);
            scanResult = Esp32Constants.TicketScanResult.ERROR;
        }

        if (lastScanLogOpt.isPresent()) {
            TicketScanLog lastScanLog = lastScanLogOpt.get();

            if (Esp32Constants.TicketScanType.CHECKIN.equals(lastScanLog.getType())) {
                scanType = Esp32Constants.TicketScanType.CHECKOUT;
            }
        }

        newLog.setUid(uid);
        newLog.setType(scanType);
        newLog.setResult(scanResult);
        newLog.setScanTime(Instant.now());
        ticketScanLogRepository.save(newLog);

        log.info("Saved scan log: uid={}, type={}, result={}, error={}", uid, scanType, scanResult, error);
    }

    @Override
    @Transactional
    public BaseResponseDTO createNewTicket() {
        Message message = new Message();
        String uid = Common.generateUniqueUid();

        while (ticketRepository.existsByUid(uid)) {
            uid = Common.generateUniqueUid();
        }

        message.setMessage(uid);
        message.setAction(Esp32Constants.Action.WRITE_NFC);
        mqttProducer.sendToEsp32(JsonUtils.toJsonString(message));

        return BaseResponseDTO.builder().ok(uid);
    }

    @Override
    public BaseResponseDTO getAllTicketsWithPaging(TicketFilterRequestDTO request) {
        Page<ITicketDTO> ticketsWithPaged = ticketRepository.findAllWithPaging(
            request.getStatusSearch(Esp32Constants.TicketStatus.PATTERN),
            request.getKeywordSearch(),
            request.getFromDateSearch(),
            request.getToDateSearch(),
            request.getPageable()
        );

        return BaseResponseDTO.builder().total(ticketsWithPaged.getTotalElements()).ok(ticketsWithPaged.getContent());
    }

    @Override
    @Transactional
    public BaseResponseDTO updateTicketStatus(String uid, String status) {
        ticketRepository.updateTicketStatus(uid, status);
        return BaseResponseDTO.builder().ok();
    }

    @Override
    @Transactional
    public BaseResponseDTO deleteTicket(String uid) {
        if (Objects.isNull(uid)) {
            throw new BaseBadRequestException(ENTITY_NAME, ExceptionConstants.INVALID_REQUEST_DATA);
        }

        ticketRepository.updateTicketStatus(uid, Esp32Constants.TicketStatus.DELETED);
        return BaseResponseDTO.builder().ok();
    }

    @Override
    public BaseResponseDTO getAllScanLogsWithPaging(TicketScanLogFilterRequestDTO request) {
        Page<ITicketScanLogDTO> logsWithPaged = ticketScanLogRepository.findAllWithPaging(
            request.getTypeSearch(),
            request.getResultTypeSearch(),
            request.getFromDateSearch(),
            request.getToDateSearch(),
            request.getPageable()
        );

        return BaseResponseDTO.builder().total(logsWithPaged.getTotalElements()).ok(logsWithPaged.getContent());
    }

    @Override
    public BaseResponseDTO getTicketScanLogsStatistic(TicketScanLogStatisticRequestDTO request) {
        List<TicketScanLogStatisticDTO> results = ticketScanLogRepository.getLogStats(request);
        return BaseResponseDTO.builder().ok(results);
    }
}
