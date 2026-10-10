import RMI.DataService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class RMI_Data1 {

    public static void main(String[] args) {
        String host = "36.50.135.242";
        String studentCode = "B21DCCN001"; // TODO: thay bằng mã sinh viên của bạn
        String qCode = "A1B2C3D4";         // TODO: thay bằng mã câu hỏi của đề bạn nhận

        try {
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            DataService service = (DataService) registry.lookup("RMIDataService");

            List<Integer> arr = service.requestData(studentCode, qCode);
            System.out.println("Du lieu nhan duoc: " + arr);

            List<Integer> peaks = new ArrayList<>();
            int n = arr.size();
            for (int i = 0; i < n; i++) {
                boolean leftOk = (i == 0) || arr.get(i) > arr.get(i - 1);
                boolean rightOk = (i == n - 1) || arr.get(i) > arr.get(i + 1);
                if (leftOk && rightOk) {
                    peaks.add(i + 1); // chỉ số bắt đầu từ 1
                }
            }
            System.out.println("Cac dinh cuc bo (chi so tu 1): " + peaks);

            service.submitData(studentCode, qCode, peaks);
            System.out.println("Da gui ket qua thanh cong.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/*
 * ĐỀ BÀI:
 * Một chương trình RMI Server cung cấp giao diện DataService trong package RMI,
 * được đăng ký với RegistryServer bằng tên RMIDataService.
 *
 * Yêu cầu:
 * a. Sử dụng mã sinh viên và mã câu hỏi làm tham số khi gọi RMI.
 *    Ví dụ: studentCode="B21DCCN001", qCode="A1B2C3D4".
 * b. Triệu gọi requestData(studentCode, qCode) để nhận một mảng số nguyên từ server.
 *    Ví dụ: [3,8,5,10,7].
 * c. Một phần tử là đỉnh cục bộ nếu lớn hơn phần tử liền kề bên trái và bên phải;
 *    phần tử đầu/cuối chỉ cần lớn hơn phần tử kề duy nhất.
 *    Trả về vị trí theo chỉ số bắt đầu từ 1.
 * d. Triệu gọi submitData(studentCode, qCode, data) để gửi List<Integer> các vị trí
 *    đỉnh cục bộ. Ví dụ: [2,4].
 * e. Kết thúc chương trình client.
 */