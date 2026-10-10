import RMI.DataService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Locale;

public class RMI_Data2 {

    public static void main(String[] args) {
        String host = "36.50.135.242";
        String studentCode = "B21DCCN001"; // TODO: thay bằng mã sinh viên của bạn
        String qCode = "5X16WuzM";

        try {
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            DataService service = (DataService) registry.lookup("RMIDataService");

            String csv = service.requestData(studentCode, qCode);
            System.out.println("Du lieu nhan duoc: " + csv);

            String[] parts = csv.trim().split("\\s*,\\s*");
            int n = parts.length;
            double[] arr = new double[n];
            for (int i = 0; i < n; i++) {
                arr[i] = Double.parseDouble(parts[i]);
            }

            double sum = 0;
            for (double v : arr) sum += v;
            double avg = sum / n;

            double sq = 0;
            for (double v : arr) sq += (v - avg) * (v - avg);
            double stddev = Math.sqrt(sq / n); // độ lệch chuẩn tổng thể

            double[] sorted = arr.clone();
            Arrays.sort(sorted);
            int idx = (int) Math.ceil(n * 0.95) - 1;
            double p95 = sorted[idx];

            String result = String.format(Locale.US,
                    "average=%.2f;stddev=%.2f;p95=%.2f", avg, stddev, p95);
            System.out.println("Ket qua gui len: " + result);

            service.submitData(studentCode, qCode, result);
            System.out.println("Da gui ket qua thanh cong.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/*
 * ĐỀ BÀI:
 * Mã câu hỏi: 5X16WuzM
 * Exam Server: 36.50.135.242
 *
 * Một chương trình RMI Server cung cấp giao diện DataService trong package RMI,
 * được đăng ký với RegistryServer bằng tên RMIDataService.
 *
 * Yêu cầu:
 * a. Sử dụng mã sinh viên và mã câu hỏi làm tham số khi gọi RMI.
 *    Ví dụ: studentCode="B21DCCN001", qCode="A1B2C3D4".
 * b. Triệu gọi requestData(studentCode, qCode) để nhận chuỗi CSV các số thực.
 *    Ví dụ: 10.00,20.00,30.00,40.00.
 * c. Tính giá trị trung bình, độ lệch chuẩn trên toàn bộ dãy và phân vị 95 tại vị trí
 *    ceil(n * 0.95) - 1 sau khi sắp xếp tăng dần.
 * d. Triệu gọi submitData(studentCode, qCode, data) để gửi chuỗi
 *    average=<avg>;stddev=<stddev>;p95=<p95>, các số thực làm tròn 02 chữ số thập phân.
 *    Ví dụ: average=25.00;stddev=11.18;p95=40.00.
 * e. Kết thúc chương trình client.
 */