import RMI.ObjectService;
import RMI.TicketSla;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMI_Object {

    public static void main(String[] args) {
        String host = "36.50.135.242";
        String studentCode = "B21DCCN001"; // TODO: thay bằng mã sinh viên của bạn
        String qCode = "lzkJfYLM";

        try {
            // a. Kết nối RegistryServer (cổng mặc định 1099), tra cứu đối tượng từ xa
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            ObjectService service = (ObjectService) registry.lookup("RMIObjectService");

            // b. Nhận đối tượng TicketSla
            TicketSla ticket = (TicketSla) service.requestObject(studentCode, qCode);
            System.out.println("Nhan duoc: " + ticket);

            // c. Xác định vi phạm theo ngưỡng của từng mức ưu tiên
            int hours = ticket.getOpenedHoursAgo();
            boolean breached;
            switch (ticket.getPriority()) {
                case "CRITICAL": breached = hours > 2;  break;
                case "HIGH":     breached = hours > 8;  break;
                case "MEDIUM":   breached = hours > 24; break;
                case "LOW":      breached = hours > 72; break;
                default:         breached = false;
            }
            ticket.setBreached(breached);

            // Xác định action
            if (!breached) {
                ticket.setAction("MONITOR");
            } else if ("CRITICAL".equals(ticket.getPriority()) || hours > 96) {
                ticket.setAction("ESCALATE_L2");
            } else {
                ticket.setAction("ESCALATE_L1");
            }
            System.out.println("Gui len: " + ticket);

            // d. Gửi lại đối tượng đã cập nhật
            service.submitObject(studentCode, qCode, ticket);
            System.out.println("Da gui ket qua thanh cong.");

            // e. Kết thúc chương trình
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/*
 * ĐỀ BÀI:
 * Mã câu hỏi: lzkJfYLM
 * Exam Server: 36.50.135.242
 *
 * Một chương trình RMI Server cung cấp giao diện từ xa ObjectService, được đăng ký với
 * RegistryServer bằng tên RMIObjectService.
 *
 * Giao diện từ xa và lớp dữ liệu
 * Tạo interface và lớp dưới đây trong package RMI của project client, đúng tên, chữ ký
 * phương thức, serialVersionUID, tên và kiểu của từng thuộc tính.
 *
 *   package RMI;
 *
 *   public interface ObjectService extends java.rmi.Remote {
 *       java.io.Serializable requestObject(String studentCode, String qCode) throws java.rmi.RemoteException;
 *       void submitObject(String studentCode, String qCode, java.io.Serializable object) throws java.rmi.RemoteException;
 *   }
 *
 *   package RMI;
 *
 *   public class TicketSla implements java.io.Serializable {
 *       private static final long serialVersionUID = 20260517L;
 *
 *       private String ticketId;
 *       private String priority;      // LOW, MEDIUM, HIGH, CRITICAL
 *       private int openedHoursAgo;
 *       private boolean breached;
 *       private String action;
 *
 *       public TicketSla() { }
 *       public TicketSla(String ticketId, String priority, int openedHoursAgo) { // gán 3 thuộc tính }
 *       // getter/setter cho tất cả thuộc tính
 *   }
 *
 * Yêu cầu
 * a. Dùng mã sinh viên và mã câu hỏi làm tham số khi gọi RMI.
 *    Ví dụ: studentCode="B21DCCN001", qCode="5AD2B818".
 * b. Triệu gọi requestObject(studentCode, qCode) để nhận đối tượng RMI.TicketSla.
 *    Ví dụ: priority=HIGH; openedHoursAgo=10.
 * c. Thiết lập breached = true khi vượt ngưỡng theo priority:
 *    - CRITICAL: openedHoursAgo > 2
 *    - HIGH:     openedHoursAgo > 8
 *    - MEDIUM:   openedHoursAgo > 24
 *    - LOW:      openedHoursAgo > 72
 *    Thiết lập action:
 *    - MONITOR nếu không vi phạm
 *    - ESCALATE_L2 nếu vi phạm và (priority là CRITICAL hoặc openedHoursAgo > 96)
 *    - ESCALATE_L1 cho các trường hợp vi phạm còn lại
 * d. Triệu gọi submitObject(studentCode, qCode, object) để gửi lại đối tượng đã cập nhật.
 *    Ví dụ: breached=true; action=ESCALATE_L1.
 * e. Kết thúc chương trình client.
 */