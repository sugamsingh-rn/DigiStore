package email;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import util.SqlUtil;

@WebServlet("/EmailSendingServlet")
public class EmailSendingServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private String host;
	private String port;
	private String user;
	private String pass;

	public void init() {
		// reads SMTP server setting from web.xml file
		ServletContext context = getServletContext();
		host = context.getInitParameter("host");
		port = context.getInitParameter("port");
		user = context.getInitParameter("user");
		pass = context.getInitParameter("pass");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String linkId = "", recipient = "", subject = "", content = "", password = "", checkEmail = "";

		ResultSet rscheckEmail = null;
		HttpSession session = request.getSession();
		String userLoggedIn = (String) session.getAttribute("email");
		linkId = (String) session.getAttribute("linkId");

		String newEmail = request.getParameter("newEmail");

		if (newEmail.isEmpty()) {
			recipient = userLoggedIn;
			SqlUtil.connectDB();
			checkEmail = "select * from download_verification where user_email='" + userLoggedIn + "' and link_id='"
					+ linkId + "'";
			rscheckEmail = SqlUtil.read(checkEmail);

			try {
				if (rscheckEmail.next()) {
					password = rscheckEmail.getString("password");

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {

			recipient = request.getParameter("newEmail");
			SqlUtil.connectDB();

			try {
				String updateUserEmail = "update download_verification set user_email='" + recipient
						+ "' where user_email='" + userLoggedIn + "' and link_id ='" + linkId + "'";
				SqlUtil.updateData(updateUserEmail);
				checkEmail = "select * from download_verification where user_email='" + recipient + "' and link_id ='"
						+ linkId + "'";
				rscheckEmail = SqlUtil.read(checkEmail);
				if (rscheckEmail.next()) {
					password = rscheckEmail.getString("password");
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		subject = "Download link and one time password";
		content = "Thank you for purchasing this product \n URl : http://localhost:8080/DigiStore/DownloadVerification.jsp?linkId=" + linkId + "\n"
				+ "PASSWORD : " + password;
		if (user.equals("vasuchouhan4260")) {
			user = user + "@gmail.com";

		}

		try {
			EmailUtility.sendEmail(host, port, user, pass, recipient, subject, content);
			SqlUtil.close();
			response.sendRedirect("cartProcess.jsp?message1=successfull");
		} catch (MessagingException ex) {
			SqlUtil.close();
			ex.printStackTrace();
			response.sendRedirect("cartProcess.jsp?message2=unsuccessfull");
		} finally {
			SqlUtil.close();

		}

	}
}
