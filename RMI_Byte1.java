import RMI.ByteService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedHashMap;
import java.util.Map;

public class ByteClient {

    public static void main(String[] args) {
        String host = "36.50.135.242";
        String studentCode = "B21DCCN001"; // TODO: thay bằng mã sinh viên của bạn
        String qCode = "jQGxtJ4K";

        try {
            // Kết nối tới RegistryServer (cổng mặc định 1099) và tra cứu đối tượng từ xa
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            ByteService service = (ByteService) registry.lookup("RMIByteService");

            // a. Nhận mảng byte từ server
            byte[] data = service.requestData(studentCode, qCode);
            System.out.println("Du lieu nhan duoc: " + java.util.Arrays.toString(data));

            // b. Tìm phần tử xuất hiện ít nhất (nếu hòa, lấy phần tử xuất hiện đầu tiên)
            // LinkedHashMap giữ thứ tự xuất hiện đầu tiên của từng phần tử
            Map<Byte, Integer> count = new LinkedHashMap<>();
            for (byte b : data) {
                count.merge(b, 1, Integer::sum);
            }

            byte minElement = 0;
            int minCount = Integer.MAX_VALUE;
            for (Map.Entry<Byte, Integer> e : count.entrySet()) {
                // Dấu "<" (nghiêm ngặt) đảm bảo giữ phần tử xuất hiện đầu tiên khi bằng nhau
                if (e.getValue() < minCount) {
                    minCount = e.getValue();
                    minElement = e.getKey();
                }
            }

            byte[] result = new byte[]{minElement, (byte) minCount};
            System.out.println("Ket qua gui len: " + java.util.Arrays.toString(result));

            // c. Gửi kết quả lên server
            service.submitData(studentCode, qCode, result);
            System.out.println("Da gui ket qua thanh cong.");

            // d. Kết thúc chương trình
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/*
 * ĐỀ BÀI:
 * Mã câu hỏi: jQGxtJ4K
 * Exam Server: 36.50.135.242
 *
 * Một chương trình (tạm gọi là RMI Server) cung cấp giao diện cho phép triệu gọi từ xa
 * để xử lý dữ liệu nhị phân.
 * Giao diện từ xa:
 *
 *   public interface ByteService extends Remote {
 *       public byte[] requestData(String studentCode, String qCode) throws RemoteException;
 *       public void submitData(String studentCode, String qCode, byte[] data) throws RemoteException;
 *   }
 *
 * Trong đó:
 *   - Interface ByteService được viết trong package RMI.
 *
 * Đối tượng cài đặt giao diện từ xa ByteService được đăng ký với RegistryServer
 * với tên là: RMIByteService.
 *
 * Yêu cầu: Viết chương trình tại máy trạm (RMI client) để thực hiện các công việc sau
 * với dữ liệu nhị phân nhận được từ RMI Server:
 *   a. Triệu gọi phương thức requestData để nhận một mảng dữ liệu nhị phân (byte[]) từ server.
 *   b. Tìm phần tử có số lần xuất hiện ít nhất trong mảng, nếu có nhiều phần tử có cùng số lần
 *      xuất hiện ít nhất, chỉ cần trả về phần tử đầu tiên xuất hiện trong các phần tử đó.
 *      Ví dụ: Nếu mảng dữ liệu nhận được là [1, 2, 3, 2, 1], chương trình sẽ tìm ra phần tử
 *      xuất hiện ít nhất là 3 (xuất hiện 1 lần), nên mảng gửi lên server là [3, 1].
 *   c. Triệu gọi phương thức submitData để gửi mảng byte kết quả chứa phần tử có số lần
 *      xuất hiện ít nhất và số lần xuất hiện trở lại server.
 *   d. Kết thúc chương trình client.
 */