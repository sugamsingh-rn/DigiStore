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

/**
 * Servlet implementation class AddProductServlet
 */
@WebServlet("/AddProductServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, maxFileSize = 1024 * 1024 * 50, maxRequestSize = 1024 * 1024
		* 100)

public class AddProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String DBURL = "jdbc:mysql://localhost:3306/";
	private static final String DBNAME = "DigiStore";
	private static final String DBPASS = "root";
	private static final String DBUSER = "root";
	private static final String UPLOAD_DIR = "images";

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Connection con = null;
		PreparedStatement pst = null;
		String productId = request.getParameter("productId");
		String productName = request.getParameter("productName");

		Float price = Float.parseFloat(request.getParameter("price"));
		String description = request.getParameter("description");
		String category = request.getParameter("category");

		String applicationPath = getServletContext().getRealPath("");
		String applicationPthForFile = getServletContext().getRealPath("file");
		String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

		File fileSaveDir = new File(applicationPthForFile);
		if (!(fileSaveDir.exists())) {
			fileSaveDir.mkdirs();
		}

		File imageSaveDir = new File(uploadFilePath);
		if (!(imageSaveDir.exists())) {
			imageSaveDir.mkdirs();
		}

//		Generating file name
		String imageExtension = ".jpg";
		String fileExtension = ".zip";
		String imageName = Long.toString(Calendar.getInstance().getTimeInMillis()) + imageExtension;
		String fileName = Long.toString(Calendar.getInstance().getTimeInMillis()) + fileExtension;
		String path = uploadFilePath + File.separator + imageName;
		String filePath = applicationPthForFile + File.separator + fileName;
//		Get image and save inn file
		Part part = request.getPart("image");
		part.write(path);

		Part filePart = request.getPart("zipFile");

		filePart.write(filePath);

		try {
			if (description.isEmpty()) {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(DBURL + DBNAME, DBUSER, DBPASS);
				pst = con.prepareStatement("insert into product values(?,?,?,?,?,default,?)");
				pst.setString(1, productId);
				pst.setString(2, productName);
				pst.setFloat(3, price);
				pst.setString(4, File.separator + imageName);
				pst.setString(5, category);
				pst.setString(6, File.separator + fileName);
				pst.executeUpdate();
			}

			else {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(DBURL + DBNAME, DBUSER, DBPASS);
				pst = con.prepareStatement("insert into product values(?,?,?,?,?,?,?)");
				pst.setString(1, productId);
				pst.setString(2, productName);
				pst.setFloat(3, price);
				pst.setString(4, File.separator + imageName);
				pst.setString(5, category);
				pst.setString(6, description);
				pst.setString(7, File.separator + fileName);
				pst.executeUpdate();
			}
			con.close();
			pst.close();
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
