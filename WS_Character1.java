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

public class WS_Character1 {

    // Dịch vụ khác DataService nên endpoint đổi sang CharacterService
    static final String ENDPOINT = "http://36.50.135.242:2221/CharacterService";
    // TODO: kiểm tra targetNamespace trong CharacterService?wsdl (thường giống DataService)
    static final String NS = "http://soap.server.network.judge.dblab.ptit/";

    public static void main(String[] args) {
        String studentCode = "B21DCCN001"; // TODO: thay bằng mã sinh viên của bạn
        String qCode = "thpHR2Nx";

        try {
            // a. Gọi requestCharacter, nhận danh sách ký tự (dạng số nguyên)
            String body = "<ns:requestCharacter xmlns:ns=\"" + NS + "\">"
                    + "<studentCode>" + studentCode + "</studentCode>"
                    + "<qCode>" + qCode + "</qCode>"
                    + "</ns:requestCharacter>";
            String response = callSoap(body, "requestCharacter");

            List<Integer> data = new ArrayList<>();
            Matcher m = Pattern.compile("<return>\\s*(-?\\d+)\\s*</return>").matcher(response);
            while (m.find()) {
                data.add(Integer.parseInt(m.group(1)));
            }
            System.out.println("Du lieu nhan duoc: " + data);

            // b. Xoay vòng phải k lần, k = mã ASCII của ký tự đầu tiên
            // Dùng k % n để tránh xoay thừa (xoay n lần = giữ nguyên)
            int n = data.size();
            if (n > 0) {
                int k = data.get(0);
                Collections.rotate(data, k % n); // rotate với số dương = xoay phải
            }
            System.out.println("Sau khi xoay: " + data);

            // c. Gửi mảng đã xoay bằng submitCharacterCharArray (mỗi phần tử là một thẻ <data>)
            StringBuilder items = new StringBuilder();
            for (int x : data) {
                items.append("<data>").append(x).append("</data>");
            }
            String submit = "<ns:submitCharacterCharArray xmlns:ns=\"" + NS + "\">"
                    + "<studentCode>" + studentCode + "</studentCode>"
                    + "<qCode>" + qCode + "</qCode>"
                    + items
                    + "</ns:submitCharacterCharArray>";
            callSoap(submit, "submitCharacterCharArray");
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
 * Mã câu hỏi: thpHR2Nx
 * Exam Server: 36.50.135.242
 *
 * Một dịch vụ web (hỗ trợ SOAP version 1.1) được định nghĩa và mô tả trong tệp
 * CharacterService.wsdl, được triển khai trên server tại URL
 * http://<Exam_IP>:2221/CharacterService?wsdl để xử lý các bài toán về chuỗi và ký tự.
 *
 * Yêu cầu: Viết chương trình tại máy trạm (WS client) để giao tiếp với CharacterService
 * thực hiện các công việc sau:
 * a. Triệu gọi phương thức requestCharacter với tham số đầu vào là mã sinh viên
 *    (studentCode) và mã câu hỏi (qCode) để nhận về một danh sách ký tự (List<Integer>)
 *    từ server.
 * b. Thực hiện xoay vòng các ký tự trong danh sách theo chiều phải, số lần xoay vòng
 *    bằng đúng giá trị nguyên trong bảng mã ASCII của ký tự đầu tiên.
 * c. Triệu gọi phương thức submitCharacterCharArray(String studentCode, String qCode,
 *    List<Integer> data) để gửi mảng ký tự đã xoay vòng trở lại server.
 *    Ví dụ: Nếu mảng ký tự nhận được từ phương thức requestCharacter là
 *    ['a', 'b', 'c', 'd'], giá trị nguyên của ký tự 'a' trong bảng mã ASCII là 97,
 *    thực hiện xoay vòng phải 97 lần sẽ trả lại mảng ['d', 'a', 'b', 'c'].
 *    Mảng này sẽ được gửi lại server qua phương thức submitCharacterCharArray.
 * d. Kết thúc chương trình client.
 */
