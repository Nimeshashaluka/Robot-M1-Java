package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author nimes
 */
@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();

        responseJson.addProperty("success", false);
        responseJson.addProperty("message", "Error");

        JsonObject requestJson = gson.fromJson(request.getReader(), JsonObject.class);

        String username = requestJson.get("username").getAsString();
        String password = requestJson.get("password").getAsString();

        if (username.isEmpty()) {
            responseJson.addProperty("message", "Please Enter Username");
        } else if (password.isEmpty()) {
            responseJson.addProperty("message", "Please Enter Password");
        } else {
            Session session = HibernateUtil.getSessionFactory().openSession();

            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("username", username));
            criteria1.add(Restrictions.eq("password", password));

            if (!criteria1.list().isEmpty()) {

                User user = (User) criteria1.uniqueResult();

                responseJson.addProperty("success", true);
                responseJson.addProperty("message", "Log In Success!");
                responseJson.add("user", gson.toJsonTree(user));

            } else {
                responseJson.addProperty("message", "Invalid User Details");
            }

            session.close();
        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));

    }

}
