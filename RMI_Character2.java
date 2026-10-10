import RMI.CharacterService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CharacterClient {

    public static void main(String[] args) {
        String host = "36.50.135.242";
        String studentCode = "B21DCCN001"; // TODO: thay bằng mã sinh viên của bạn
        String qCode = "NBxKQ4Mk";

        try {
            // Kết nối tới RegistryServer (cổng mặc định 1099) và tra cứu đối tượng từ xa
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            CharacterService service = (CharacterService) registry.lookup("RMICharacterService");

            // a. Nhận chuỗi chứa số thập phân từ server
            String s = service.requestCharacter(studentCode, qCode);
            System.out.println("Chuoi nhan duoc: " + s);

            // b. Chuyển số thập phân sang số La Mã
            int number = Integer.parseInt(s.trim());
            String roman = toRoman(number);
            System.out.println("So La Ma: " + roman);

            // c. Gửi chuỗi La Mã lên server
            service.submitCharacter(studentCode, qCode, roman);
            System.out.println("Da gui ket qua thanh cong.");

            // d. Kết thúc chương trình
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Chuyển số nguyên dương sang số La Mã bằng phương pháp tham lam (greedy),
    // bao gồm cả các dạng trừ: IV=4, IX=9, XL=40, XC=90, CD=400, CM=900
    private static String toRoman(int num) {
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            while (num >= values[i]) {
                sb.append(symbols[i]);
                num -= values[i];
            }
        }
        return sb.toString();
    }
}

/*
 * ĐỀ BÀI:
 * Mã câu hỏi: NBxKQ4Mk
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
 *   a. Triệu gọi phương thức requestCharacter để nhận chuỗi ngẫu nhiên từ server với định dạng:
 *      "Số thập phân đầu vào".
 *   b. Thực hiện chuyển đổi số thập phân nhận được thành chuỗi số La Mã (Roman).
 *      Quy tắc chuyển đổi:
 *      Các ký tự La Mã chính bao gồm: I=1, V=5, X=10, L=50, C=100, D=500, M=1000.
 *      Ví dụ: 58 -> "LVIII"
 *   c. Triệu gọi phương thức submitCharacter để gửi chuỗi số La Mã đã chuyển đổi trở lại server.
 *   d. Kết thúc chương trình client.
 */