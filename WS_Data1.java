import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WS_Data1 {

    // TODO: kiểm tra lại 2 dòng này bằng cách mở WSDL trong trình duyệt
    // (thuộc tính targetNamespace ở đầu file WSDL)
    static final String ENDPOINT = "http://36.50.135.242:2221/DataService";
    static final String NS = "http://soap.server.network.judge.dblab.ptit/"; // thay bằng targetNamespace thật trong WSDL

    public static void main(String[] args) {
        String studentCode = "B23DCCN994"; // TODO: thay bằng mã sinh viên của bạn
        String qCode = "dHmPUZJM";

        try {
            // a. Gọi getData, nhận danh sách số nguyên
            String body = "<ns:getData xmlns:ns=\"" + NS + "\">"
                    + "<studentCode>" + studentCode + "</studentCode>"
                    + "<qCode>" + qCode + "</qCode>"
                    + "</ns:getData>";
            String response = callSoap(body, "getData");

            // Mỗi phần tử trả về nằm trong thẻ <return>...</return>
            List<String> strs = new ArrayList<>();
            Matcher m = Pattern.compile("<return>\\s*(-?\\d+)\\s*</return>").matcher(response);
            while (m.find()) {
                strs.add(m.group(1));
            }
            System.out.println("Du lieu nhan duoc: " + strs);

            // b. Số lớn nhất ghép được: so sánh (a+b) với (b+a)
            Collections.sort(strs, (a, b) -> (b + a).compareTo(a + b));
            StringBuilder sb = new StringBuilder();
            for (String s : strs) sb.append(s);
            String result = sb.toString();
            if (result.startsWith("0")) result = "0";
            System.out.println("Ket qua gui len: " + result);

            // c. Gửi kết quả lên server bằng submitDataString
            String submit = "<ns:submitDataString xmlns:ns=\"" + NS + "\">"
                    + "<studentCode>" + studentCode + "</studentCode>"
                    + "<qCode>" + qCode + "</qCode>"
                    + "<data>" + result + "</data>"
                    + "</ns:submitDataString>";
            callSoap(submit, "submitDataString");
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
 * Mã câu hỏi: dHmPUZJM
 * Exam Server: 36.50.135.242
 *
 * Một dịch vụ web (hỗ trợ SOAP version 1.1) được định nghĩa và mô tả trong tệp
 * DataService?wsdl, được triển khai trên server tại URL
 * http://<Exam_IP>:2221/DataService?wsdl để xử lý các bài toán với dữ liệu nguyên thủy.
 *
 * Yêu cầu: Viết chương trình tại máy trạm (WS client) để giao tiếp với DataService
 * thực hiện các công việc sau:
 * a. Triệu gọi phương thức getData với tham số đầu vào là mã sinh viên (studentCode)
 *    và mã câu hỏi (qCode) để nhận về một danh sách số nguyên (List<Integer>) từ server.
 *    Ví dụ: 7602,9136,1090,34319,7830,6179,10584,20166,28199,30250,32179,22544,3222,10320,30590,19279
 * b. Thực hiện tìm số lớn nhất có thể tạo được từ a,b,c,d...
 * c. Triệu gọi phương thức submitDataString(String studentCode, String qCode, String data)
 *    để gửi kết quả tổng đã tính được trở lại server.
 *    Ví dụ: 91367830760261793431932223217930590302502819922544201661927910901058410320
 * d. Kết thúc chương trình client.
 */