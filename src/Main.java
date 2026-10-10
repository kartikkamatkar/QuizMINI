import java.sql.Connection;
public class Main {
    public static void main(String[] args) {
      try(Connection con = DBConnector.getConnection()){

              System.out.println("Database connected successfully !");
      }
          catch(Exception e){
              e.printStackTrace();
          }
      }
}