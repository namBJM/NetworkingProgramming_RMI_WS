import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WS_Character2 {

    static final String ENDPOINT = "http://36.50.135.242:2221/CharacterService";
    // TODO: kiểm tra targetNamespace trong CharacterService?wsdl (thường giống DataService)
    static final String NS = "http://soap.server.network.judge.dblab.ptit/";

    public static void main(String[] args) {
        String studentCode = "B21DCCN001"; // TODO: thay bằng mã sinh viên của bạn
        String qCode = "nlhgTYCl";

        try {
            // a. Gọi requestStringArray, nhận danh sách chuỗi
            String body = "<ns:requestStringArray xmlns:ns=\"" + NS + "\">"
                    + "<studentCode>" + studentCode + "</studentCode>"
                    + "<qCode>" + qCode + "</qCode>"
                    + "</ns:requestStringArray>";
            String response = callSoap(body, "requestStringArray");

            List<String> words = new ArrayList<>();
            Matcher m = Pattern.compile("<return>(.*?)</return>", Pattern.DOTALL).matcher(response);
            while (m.find()) {
                words.add(m.group(1).trim());
            }
            System.out.println("Du lieu nhan duoc: " + words);

            // b. Nhóm các từ theo số lượng nguyên âm (u e o a i, không phân biệt hoa thường)
            // LinkedHashMap: các nhóm xếp theo thứ tự xuất hiện đầu tiên trong danh sách
            // (muốn xếp theo số nguyên âm tăng dần thì đổi thành TreeMap)
            Map<Integer, List<String>> groups = new LinkedHashMap<>();
            for (String w : words) {
                int count = countVowels(w);
                groups.computeIfAbsent(count, k -> new ArrayList<>()).add(w);
            }

            // Sắp xếp từ trong mỗi nhóm theo thứ tự từ điển, nối bằng ", "
            List<String> result = new ArrayList<>();
            for (List<String> g : groups.values()) {
                Collections.sort(g);
                result.add(String.join(", ", g));
            }
            System.out.println("Ket qua gui len: " + result);

            // c. Gửi danh sách chuỗi kết quả (mỗi nhóm là một thẻ <data>)
            StringBuilder items = new StringBuilder();
            for (String s : result) {
                items.append("<data>").append(s).append("</data>");
            }
            String submit = "<ns:submitCharacterStringArray xmlns:ns=\"" + NS + "\">"
                    + "<studentCode>" + studentCode + "</studentCode>"
                    + "<qCode>" + qCode + "</qCode>"
                    + items
                    + "</ns:submitCharacterStringArray>";
            callSoap(submit, "submitCharacterStringArray");
            System.out.println("Da gui ket qua thanh cong.");

            // d. Kết thúc chương trình
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static int countVowels(String s) {
        int c = 0;
        for (char ch : s.toLowerCase().toCharArray()) {
            if ("aeiou".indexOf(ch) >= 0) c++;
        }
        return c;
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
 * Mã câu hỏi: nlhgTYCl
 * Exam Server: 36.50.135.242
 *
 * Một dịch vụ web (hỗ trợ SOAP version 1.1) được định nghĩa và mô tả trong tệp
 * CharacterService.wsdl, được triển khai trên server tại URL
 * http://<Exam_IP>:2221/CharacterService?wsdl để xử lý các bài toán về chuỗi và ký tự.
 *
 * Yêu cầu: Viết chương trình tại máy trạm (WS client) để giao tiếp với CharacterService
 * thực hiện các công việc sau:
 * a. Triệu gọi phương thức requestStringArray với tham số đầu vào là mã sinh viên
 *    (studentCode) và mã câu hỏi (qCode) để nhận về một danh sách chuỗi (List<String>)
 *    từ server.
 * b. Phân loại các từ trong mảng chuỗi thành các nhóm có cùng số lượng nguyên âm.
 *    Tạo một chuỗi cho mỗi nhóm, trong đó liệt kê các từ cách nhau bằng dấu phẩy,
 *    và sắp xếp các từ theo thứ tự từ điển trong mỗi nhóm.
 * c. Triệu gọi phương thức submitCharacterStringArray(String studentCode, String qCode,
 *    List<String> data) để gửi danh sách chuỗi kết quả trở lại server, trong đó mỗi
 *    phần tử là một nhóm từ với cùng số lượng nguyên âm.
 *    Ví dụ: Nếu danh sách chuỗi nhận được từ phương thức requestCharacter là
 *    [apple, banana, pear, grape, kiwi], các nhóm có thể là:
 *      - Nhóm 2 nguyên âm: apple, banana
 *      - Nhóm 1 nguyên âm: grape, kiwi, pear
 *    Danh sách kết quả sẽ là [apple, banana] và [grape, kiwi, pear], và danh sách này
 *    sẽ được gửi lại server qua phương thức submitCharacter.
 * d. Kết thúc chương trình client.
 */
