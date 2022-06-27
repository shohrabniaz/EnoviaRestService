import com.matrixone.apps.domain.util.MqlUtil;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;
/**
 * This JPO file is used for Preference Setting on Translation Widget
 * This is invoked from Preference setting utility class
 * @author Al-Helal
 */
public class UserPreferenceSetting_mxJPO {
    static final Logger logger = Logger.getLogger(UserPreferenceSetting_mxJPO.class.getName());
    /**
     * Used to update all objects having a specific value for an specific
     * attribute
     *
     * @param context
     * @param uName
     * @param args
     * @return update status string with the preferences
     * @throws java.lang.Exception
     */
    public String updateUserAttribute(Context context, String[] args) throws Exception {
        //Context context, String[] args [existing parameters]
        //mod Person jklalrahab2 property "preference_test" value {"name":"John","age":300,"car":null};
        //print Person jklalrahab2 select property[preference_test].value;
        Map map = (Map) JPO.unpackArgs(args);
        @SuppressWarnings("UnusedAssignment")
        String status = null;
        List<String> translatorsList = new ArrayList<>();
        String userName = (String) map.get("userName");
        logger.log(Level.INFO, "User name got in JPO : {0}", userName);
        Map<String, String> languagePrefMap = new LinkedHashMap<>();
        try {
            for (int idx = 0; idx < 6; idx++) {
                switch (idx) {
                    case 0:
                        translatorsList.add((String) map.get("English"));
                        languagePrefMap.put("English",(String) map.get("English"));
                        break;
                    case 1:
                        translatorsList.add((String) map.get("Chinese"));
                        languagePrefMap.put("Chinese",(String) map.get("Chinese"));
                        break;
                    case 2:
                        translatorsList.add((String) map.get("Finnish"));
                        languagePrefMap.put("Finnish",(String) map.get("Finnish"));
                        break;
                    case 3:
                        translatorsList.add((String) map.get("Czech"));
                        languagePrefMap.put("Czech",(String) map.get("Czech"));
                        break;
                    case 4:
                        translatorsList.add((String) map.get("German"));
                        languagePrefMap.put("German",(String) map.get("German"));
                        break;
                    case 5:
                        translatorsList.add((String) map.get("French"));
                        languagePrefMap.put("French",(String) map.get("French"));
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        String response = null;
        try {
            MQLCommand objMQL = new MQLCommand();
            objMQL.open(context);
            String mqlStatement = "mod Person " + userName + " property preferenceValue value {"
                    + "English" + ":'" + translatorsList.get(0) + "'" + ","
                    + "Chinese" + ":'" + translatorsList.get(1) + "'" + ","
                    + "Finnish" + ":'" + translatorsList.get(2) + "'" + ","
                    + "Czech" + ":'" + translatorsList.get(3) + "'" + ","
                    + "German" + ":'" + translatorsList.get(4) + "'" + ","
                    + "French" + ":'" + translatorsList.get(5) + "'" + "};";
            response = MqlUtil.mqlCommand(context, objMQL, mqlStatement);
            logger.log(Level.INFO, "mql query : {0}", mqlStatement);
            if (response.isEmpty()) {
                status = ("successfully updated");
            } else {
                status = "Not updated";
            }
            logger.log(Level.INFO, "status : {0}", status);
        } catch (MatrixException e) {
            return e.getMessage();
        }
        return status + "," + response + "," + languagePrefMap ;
    } 


	/**
     * This method is called for getting user preferences
     * @param context
     * @param uName
     * @param args
     * @return update status string
     * @throws java.lang.Exception
     */
    public String getUserAttribute(Context context, String args[]) throws Exception {
        //mod Person jklalrahab2 property "preference_test" value {"name":"John","age":300,"car":null};
        //print Person jklalrahab2 select property[preference_test].value;
        Map map = (Map) JPO.unpackArgs(args);
        String userName = (String) map.get(context.getUser());
        @SuppressWarnings("UnusedAssignment")
        String status = null;
        String response = null;
        //List<String> translatorsList = new ArrayList<>();
        try {
            MQLCommand objMQL = new MQLCommand();
            objMQL.open(context);
            String mqlStatement = "print Person " + userName
                    + " select property[preferenceValue].value;";
            response = MqlUtil.mqlCommand(context, objMQL, mqlStatement);
        } catch (MatrixException e) {
            return e.getMessage();
        }
        return response;
    }

}


