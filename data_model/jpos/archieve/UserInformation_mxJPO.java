




import com.matrixone.apps.domain.util.MqlUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

/**
 *
 * @author Suvonkar Kundu
 */
public class UserInformation_mxJPO {

    public static final Logger LOGGER = Logger.getLogger(UserInformation_mxJPO.class.getName());
    private final String SUCCESS = "Success";
    private final String ERROR = "Error";
    private final String TYPE = "Type";
    private final String NAME = "Name";
    private final String REVISION = "Revision";
    private final String PHYSICALID = "PhysicalId";
    private final String OWENR = "Owner";
    private final String Marketing_Text = "Marketing Text";
    private final String EMAIL_NOT_EXITS = "Email address not found";
    private final String PERSON_NOT_EXIT = "Person Not Exit";

    /**
     * This function get user email from user
     *
     * @param context
     * @param args
     * @return Map<String, List<String>> allUserEmail
     * @throws java.lang.Exception
     */
    public List<String> getUserEmail(Context context, String[] args) throws Exception {

        MQLCommand objMQL = new MQLCommand();
        HashMap<String, String> object = new HashMap<String, String>();
        List<String> response = new ArrayList<>();
        objMQL.open(context);
        List<String> userList = JPO.unpackArgs(args);
        for (String user : userList) {
            try {
                String sMQLStatementtemp = "temp query bus Person " + user + " - dump";
                String tempResult = MqlUtil.mqlCommand(context, objMQL, sMQLStatementtemp);
                if (tempResult.isEmpty() || tempResult == null) {
                    object = buildResponseObject(user, null, ERROR, PERSON_NOT_EXIT);
                } else {
                    String sMQLStatement = "pri bus Person " + user + " - select attribute[Email Address]";
                    String userEmail = MqlUtil.mqlCommand(context, objMQL, sMQLStatement);
                    String[] emailWithUser = userEmail.split("-");
                    String[] email = emailWithUser[1].split("=");
                    if (email[1].isEmpty() || email[1] == null || email[1].equals("")) {
                        object = buildResponseObject(user, null, ERROR, EMAIL_NOT_EXITS);
                    } else {
                        object = buildResponseObject(user, email[1].trim(), SUCCESS, null);
                    }
                }
            } catch (MatrixException ex) {

                object = buildResponseObject(user, null, ERROR, ex.toString());
            }
            response.add(object.toString());
        }

        return response;
    }

    /**
     * This function build hashMap response object
     *
     * @param user
     * @param email
     * @param status
     * @param message
     * @return HashMap<String,String> object
     */
    public HashMap<String, String> buildResponseObject(String user, String email, String status, String message) {
        HashMap<String, String> object = new HashMap<String, String>();
        object.put("user", user);
        object.put("email", email);
        object.put("status", status);
        object.put("message", message);
        return object;

    }

    public List<String> getObjectInformation(Context context, String[] args) throws Exception {

        MQLCommand objMQL = new MQLCommand();
        String sMQLStatement;
        HashMap<String, String> object = new HashMap<String, String>();
        List<String> response = new ArrayList<String>();
        HashMap<String, String> mapvalue = JPO.unpackArgs(args);
        objMQL.open(context);
        sMQLStatement = "temp query bus " + quote(mapvalue.get("Type")) + "* * limit " + mapvalue.get("Limit") + " select physicalId owner attribute[Marketing Text]  dump ";
        String objectInformation = MqlUtil.mqlCommand(context, objMQL, sMQLStatement);
        Scanner scLine = new Scanner(objectInformation);
        String lineobject;
        while (scLine.hasNext()) {
            lineobject = scLine.nextLine();
            String[] objectValue = lineobject.split(",");
            int count = 1;
            for (String value : objectValue) {
                switch (count) {
                    case 1:
                        object.put(TYPE, value);
                        break;
                    case 2:
                        object.put(NAME, value);
                        break;
                    case 3:
                        object.put(REVISION, value);
                        break;
                    case 4:
                        object.put(PHYSICALID, value);
                        break;
                    case 5:
                        object.put(OWENR, value);
                        break;
                    default:
                        object.put(Marketing_Text, value);
                        break;

                }
                count++;
            }
            response.add(object.toString());
        }
        return response;
    }

    public static String quote(String type) {
        return new StringBuilder()
                .append('\'')
                .append(type)
                .append('\'')
                .toString();
    }
}

