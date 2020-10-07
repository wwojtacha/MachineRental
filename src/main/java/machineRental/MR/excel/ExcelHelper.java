package machineRental.MR.excel;

import java.io.IOException;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExcelHelper {


  public boolean isProperFileType(MultipartFile file) {
    String extension = FilenameUtils.getExtension(file.getOriginalFilename());
    boolean isProperFile = false;
    if ("xls".equalsIgnoreCase(extension) || "xlsx".equalsIgnoreCase(extension)) {
      isProperFile = true;
    } else {
      isProperFile = false;
      throw new WrongFileTypeException("Only Excel files can be uploaded!");
    }
    return isProperFile;
  }

  public Workbook getWorkBook(MultipartFile file) {
    Workbook workbook = null;
    String extension = FilenameUtils.getExtension(file.getOriginalFilename());

    try {
      if ("xlsx".equalsIgnoreCase(extension)) {
        workbook = new XSSFWorkbook(file.getInputStream());
      } else if ("xls".equalsIgnoreCase(extension)) {
        workbook = new HSSFWorkbook(file.getInputStream());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return workbook;
  }
}
