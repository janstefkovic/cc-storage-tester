import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TestRunner {

    private static void parseObject(JSONObject object)
    {
        //Get object within list
        //JSONObject item = (JSONObject) object.get("id");

        long id = (long) object.get("id");
        System.out.println(id);

        String email = (String) object.get("email");
        System.out.println(email);

    }


    @Before
    public void readAndStoreJsonFile() {
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("MOCK_DATA.json"))
        {
            Object obj = jsonParser.parse(reader);
            JSONArray objectList = (JSONArray) obj;
            System.out.println(objectList);

            objectList.forEach( item -> parseObject( (JSONObject) item ) );

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    //public void createRingBufferStringsOfCapacity3() {
     //   rb = new RingBuffer<>(3);
  //  }

    @Test
    public void readJsonFile() {
        JSONObject fifthObject = new JSONObject();
        fifthObject.put("id",5);
        fifthObject.put("email","ltynnan4@1und1.de");

        JSONArray objectList = new JSONArray();
        objectList.add(fifthObject);

        System.out.println(objectList.toJSONString());
    }

    //public void testEmptynessOfNewRingBuffer() {
        //assert(rb.isEmpty());
    //}
}
