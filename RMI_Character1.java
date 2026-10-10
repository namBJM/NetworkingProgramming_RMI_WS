import RMI.CharacterService;

import java.net.URI;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CharacterClient {

    public static void main(String[] args) {
        String host = "36.50.135.242";
        String studentCode = "B21DCCN001"; // TODO: thay bằng mã sinh viên của bạn
        String qCode = "CRL8xXMZ";

        try {
            // Kết nối tới RegistryServer (cổng mặc định 1099) và tra cứu đối tượng từ xa
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            CharacterService service = (CharacterService) registry.lookup("RMICharacterService");

            // a. Nhận chuỗi từ server
            String s = service.requestCharacter(studentCode, qCode);
            System.out.println("Chuoi nhan duoc: " + s);

            // b. Mã hóa URL theo quy tắc phần đường dẫn của URI
            // Constructor nhiều tham số của URI tự động quote các ký tự không hợp lệ
            // (dấu cách, % " < > [ ] { } | \ ^, ?, #, ký tự ngoài ASCII) thành %XX (UTF-8)
            String encoded = new URI(null, null, s, null).toASCIIString();
            System.out.println("Chuoi ma hoa: " + encoded);

            // c. Gửi chuỗi đã mã hóa lên server
            service.submitCharacter(studentCode, qCode, encoded);
            System.out.println("Da gui ket qua thanh cong.");

            // d. Kết thúc chương trình
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/*
 * ĐỀ BÀI:
 * Mã câu hỏi: CRL8xXMZ
 * Exam Server: 36.50.135.242
 *
 * Một chương trình (tạm gọi là RMI Server) cung cấp giao diện cho phép triệu gọi từ xa
 * để xử lý chuỗi.
 * Giao diện từ xa:
 *
 *   public interface CharacterService extends Remote {
 *       public String requestCharacter(String studentCode, String qCode) throws RemoteException;
 *       public void submitCharacter(String studentCode, String qCode, String strSubmit) throws RemoteException;
 *   }
 *
 * Trong đó:
 *   - Interface CharacterService được viết trong package RMI.
 *   - Đối tượng cài đặt giao diện từ xa CharacterService được đăng ký với RegistryServer
 *     với tên là: RMICharacterService.
 *
 * Yêu cầu: Viết chương trình tại máy trạm (RMI client) để thực hiện các công việc sau
 * với chuỗi được nhận từ RMI Server:
 *   a. Triệu gọi phương thức requestCharacter để nhận chuỗi ngẫu nhiên từ server. Chuỗi này
 *      gồm chữ, số, dấu cách và các ký tự đặc biệt như `~=+%^*&#47;()[]{}!@#$?|`.
 *   b. Thực hiện thao tác mã hóa URL (URL Encoding) cho chuỗi đầu vào nhận được từ server.
 *      Mã hóa theo quy tắc phần đường dẫn của URI (tương đương
 *      new java.net.URI(null, null, s, null).toASCIIString()): dấu cách, ký tự không hợp lệ
 *      trong URI (như % " < > [ ] { } | \ ^), ?, # và ký tự ngoài ASCII được thay bằng %XX
 *      (UTF-8); chữ, số và - _ . ~ ! * ' ( ) ; : @ & = + $ , / giữ nguyên.
 *      Ví dụ: Chuỗi ban đầu "Hello World!" -> Chuỗi mã hóa URL là: "Hello%20World!"
 *   c. Triệu gọi phương thức submitCharacter để gửi chuỗi đã được mã hóa trở lại server.
 *   d. Kết thúc chương trình client.
 */