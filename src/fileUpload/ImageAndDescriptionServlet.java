package fileUpload;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/ImageAndDescriptionServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, maxFileSize = 1024 * 1024 * 50, maxRequestSize = 1024 * 1024
		* 100)

public class ImageAndDescriptionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String DBURL = "jdbc:mysql://localhost:3306/";
	private static final String DBNAME = "DigiStore";
	private static final String DBPASS = "root";
	private static final String DBUSER = "root";
	private static final String UPLOAD_DIR = "images";

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Connection con = null;
		PreparedStatement pst = null;
		String productId = request.getParameter("productId");
		String description = request.getParameter("description");

		String applicationPath = getServletContext().getRealPath("");
		String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

		File fileSaveDir = new File(uploadFilePath);
		if (!(fileSaveDir.exists())) {
			fileSaveDir.mkdirs();
		}

//		Generating file name
		String fileExtension = ".jpg";
		String fileName = Long.toString(Calendar.getInstance().getTimeInMillis()) + fileExtension;
		String path = uploadFilePath + File.separator + fileName;
//		Get image and save inn file
		Part part = request.getPart("image");
		part.write(path);

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(DBURL + DBNAME, DBUSER, DBPASS);

			if (description.isEmpty()) {
				pst = con.prepareStatement("insert into image_and_description values(?,?,default)");
				pst.setString(1, productId);
				pst.setString(2, File.separator + fileName);
				pst.executeUpdate();
			} else {
				pst = con.prepareStatement("insert into image_and_description values(?,?,?)");
				pst.setString(1, productId);
				pst.setString(2, File.separator + fileName);
				pst.setString(3, description);
				pst.executeUpdate();
			}

		} catch (Exception e) {
			System.out.println("Exception " + e);
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				pst.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		response.sendRedirect("AdminHome.jsp");

	}

}
