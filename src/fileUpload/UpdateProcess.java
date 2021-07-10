package fileUpload;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import util.SqlUtil;

@WebServlet("/UpdateProcess")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, maxFileSize = 1024 * 1024 * 50, maxRequestSize = 1024 * 1024
		* 100)
public class UpdateProcess extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String DBURL = "jdbc:mysql://localhost:3306/";
	private static final String DBNAME = "DigiStore";
	private static final String DBPASS = "root";
	private static final String DBUSER = "root";
	private static final String UPLOAD_DIR = "images";

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection con = null;
		PreparedStatement prs = null;
		String product_id = null, product_zip_file = null, product_name = null, product_image = null,
				product_description = null, category_name = null, imageName = null, zipFileName = null;
		float product_price = 0;
		String productId = request.getParameter("productId");

		SqlUtil.connectDB();
		String getAllProduct = "select * from product where product_id='" + productId + "'";
		ResultSet rsgetAllProduct = SqlUtil.read(getAllProduct);
		try {
			if (rsgetAllProduct.next()) {
				product_id = rsgetAllProduct.getString("product_id");
				product_name = rsgetAllProduct.getString("product_name");
				product_price = Float.parseFloat(rsgetAllProduct.getString("product_price"));
				product_image = rsgetAllProduct.getString("product_image");
				category_name = rsgetAllProduct.getString("category_name");
				product_description = rsgetAllProduct.getString("product_description");
				product_zip_file = rsgetAllProduct.getString("filename");
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String productName = request.getParameter("productName");
		if (!(productName.isEmpty())) {
			product_name = productName;
		}
		String price = request.getParameter("price");
		if (!(price.isEmpty())) {
			product_price = Float.parseFloat(price);
		}
		String description = request.getParameter("description");
		if (!(description.isEmpty())) {
			product_description = description;
		}
		String category = request.getParameter("category");
		if (!(category.equals("default"))) {
			category_name = category;
		}

		Part checkImagePart = request.getPart("image");
		long size = checkImagePart.getSize();
		if (size != 0) {
			String applicationPath = getServletContext().getRealPath("");
			String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

			File imageSaveDir = new File(uploadFilePath);
			if (!(imageSaveDir.exists())) {
				imageSaveDir.mkdirs();
			}

//			Generating file name
			String imageExtension = ".jpg";
			imageName = Long.toString(Calendar.getInstance().getTimeInMillis()) + imageExtension;
			String imagePath = uploadFilePath + File.separator + imageName;
//			Get image and save inn file
			Part Imagepart = request.getPart("image");

			Imagepart.write(imagePath);
			product_image = File.separator + imageName;
		}

		Part zipFilePart = request.getPart("zipFile");

		long zipFilSize = zipFilePart.getSize();
		if (zipFilSize != 0) {
			String applicationPathForFile = getServletContext().getRealPath("file");

			File zipFileSaveDir = new File(applicationPathForFile);
			if (!(zipFileSaveDir.exists())) {
				zipFileSaveDir.mkdirs();
			}

//			Generating file name
			String zipFileExtension = ".zip";
			zipFileName = Long.toString(Calendar.getInstance().getTimeInMillis()) + zipFileExtension;
			String zipFilePath = applicationPathForFile + File.separator + zipFileName;
//			Get image and save inn file

			zipFilePart.write(zipFilePath);
			product_zip_file = File.separator + zipFileName;
		}

		try {

			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(DBURL + DBNAME, DBUSER, DBPASS);
			String update = "update product set product_name=? , product_price=? , product_description=? , category_name =? , product_image  =? ,filename=? where product_id=?";
			prs = con.prepareStatement(update);
			prs.setString(1, product_name);
			prs.setFloat(2, product_price);
			prs.setString(3, product_description);
			prs.setString(4, category_name);
			prs.setString(5, product_image);
			prs.setString(6, product_zip_file);
			prs.setString(7, product_id);
			prs.executeUpdate();
			response.sendRedirect("AdminHome.jsp");
		} catch (Exception e) {
			System.out.println("Exception " + e);
		}

	}
}
