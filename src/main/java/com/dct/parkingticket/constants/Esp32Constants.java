package com.dct.parkingticket.constants;

public interface Esp32Constants {

    interface Action {
        int WRITE_NFC = 1;
        int READ_TICKET_ACTIVE = 2;
        int READ_TICKET_LOCKED = 3;
        int READ_TICKET_INVALID = 4;
        int READ_TICKET_NOT_FOUND = 5;
    }

    interface TicketStatus {
        String ACTIVE = "ACTIVE";
        String LOCKED = "LOCKED";
        String DELETED = "DELETED";
        String PATTERN = "^(ACTIVE|LOCKED|DELETED)$";
    }

    interface RequestAction {
        int SCAN_TICKET_NFC = 1;
        int RESPONSE_RESULT_WRITE_NFC = 2;
        int RESPONSE_RESULT_WRITE_NFC_ERROR = 3;
    }

    interface Response {
        String TICKET_ACTIVE = "Thẻ hợp lệ";
        String TICKET_LOCKED = "Thẻ bị khóa";
        String TICKET_INVALID = "Thẻ không hợp lệ";
        String TICKET_NOT_FOUND = "Không tìm thấy thông tin thẻ";
    }

    interface TicketScanType {
        String CHECKIN = "CHECKIN";
        String CHECKOUT = "CHECKOUT";
        String PATTERN = "^(CHECKIN|CHECKOUT)$";
    }

    interface TicketScanResult {
        String VALID = "SCAN_VALID";
        String ERROR = "SCAN_ERROR";
        String PATTERN = "^(SCAN_VALID|SCAN_ERROR)$";
    }

    String GROUP_TYPE_PATTERN = "^(MONTH|DAY|HOURS)$";
}
