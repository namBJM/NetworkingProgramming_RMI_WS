import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WS_Data2 {

    static final String ENDPOINT = "http://36.50.135.242:2221/DataService";
    static final String NS = "http://soap.server.network.judge.dblab.ptit/";

    public static void main(String[] args) {
        String studentCode = "B21DCCN001"; // TODO: thay bằng mã sinh viên của bạn
        String qCode = "THAY_MA_CAU_HOI"; // TODO: thay bằng mã câu hỏi của đề bạn nhận

        try {
            // a. Gọi getData, nhận danh sách số nguyên
            String body = "<ns:getData xmlns:ns=\"" + NS + "\">"
                    + "<studentCode>" + studentCode + "</studentCode>"
                    + "<qCode>" + qCode + "</qCode>"
                    + "</ns:getData>";
            String response = callSoap(body, "getData");

            List<Integer> data = new ArrayList<>();
            Matcher m = Pattern.compile("<return>\\s*(-?\\d+)\\s*</return>").matcher(response);
            while (m.find()) {
                data.add(Integer.parseInt(m.group(1)));
            }
            System.out.println("Du lieu nhan duoc: " + data);

            // b. Tính tổng các phần tử
            int sum = 0;
            for (int x : data) {
                sum += x;
            }
            System.out.println("Tong: " + sum);

            // c. Gửi kết quả lên server bằng submitDataInt
            String submit = "<ns:submitDataInt xmlns:ns=\"" + NS + "\">"
                    + "<studentCode>" + studentCode + "</studentCode>"
                    + "<qCode>" + qCode + "</qCode>"
                    + "<data>" + sum + "</data>"
                    + "</ns:submitDataInt>";
            callSoap(submit, "submitDataInt");
            System.out.println("Da gui ket qua thanh cong.");

            // d. Kết thúc chương trình
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Gửi một SOAP 1.1 request và trả về nội dung phản hồi dạng chuỗi
    static String callSoap(String bodyXml, String action) throws Exception {
        String envelope = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<soapenv:Body>" + bodyXml + "</soapenv:Body>"
                + "</soapenv:Envelope>";

        HttpURLConnection conn = (HttpURLConnection) new URL(ENDPOINT).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("SOAPAction", "\"" + action + "\"");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(envelope.getBytes(StandardCharsets.UTF_8));
        }

        int code = conn.getResponseCode();
        InputStream is = (code >= 400) ? conn.getErrorStream() : conn.getInputStream();
        String resp = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        if (code >= 400) {
            throw new RuntimeException("HTTP " + code + ": " + resp);
        }
        return resp;
    }
}

/*
 * ĐỀ BÀI:
 * Một dịch vụ web (hỗ trợ SOAP version 1.1) được định nghĩa và mô tả trong tệp
 * DataService?wsdl, được triển khai trên server tại URL
 * http://<Exam_IP>:2221/DataService?wsdl để xử lý các bài toán với dữ liệu nguyên thủy.
 *
 * Yêu cầu: Viết chương trình tại máy trạm (WS client) để giao tiếp với DataService
 * thực hiện các công việc sau:
 * a. Triệu gọi phương thức getData với tham số đầu vào là mã sinh viên (studentCode)
 *    và mã câu hỏi (qCode) để nhận về một danh sách số nguyên (List<Integer>) từ server.
 * b. Thực hiện tính toán tổng của tất cả các phần tử trong danh sách số nguyên nhận được.
 * c. Triệu gọi phương thức submitDataInt(String studentCode, String qCode, int data)
 *    để gửi kết quả tổng đã tính được trở lại server.
 *    Ví dụ: Nếu mảng số nguyên nhận được từ phương thức getData là [1, 2, 3, 4, 5],
 *    chương trình client sẽ tính tổng là 15 và gửi kết quả này trở lại server qua
 *    phương thức submitData.
 * d. Kết thúc chương trình client.
 */
