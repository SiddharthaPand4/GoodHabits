package io.synlabs.synvision.service.avc;

import com.querydsl.jpa.impl.JPAQuery;
import io.synlabs.synvision.entity.avc.AvcEvent;
import io.synlabs.synvision.entity.avc.QAvcEvent;
import io.synlabs.synvision.entity.avc.QSurvey;
import io.synlabs.synvision.entity.avc.Survey;
import io.synlabs.synvision.ex.NotFoundException;
import io.synlabs.synvision.jpa.SurveyRepository;
import io.synlabs.synvision.service.BaseService;
import io.synlabs.synvision.views.avc.AvcSurveyReport;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AvcReportService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(AvcReportService.class);

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private EntityManager entityManager;

    @Value("${file.upload-dir}")
    private String uploadDirPath;

    public String downloadAvcReportBySurvey(Long surveyId) throws IOException {
        Optional<Survey> opSurvey = surveyRepository.findById(surveyId);
        if (opSurvey.isPresent()) {
            Survey survey = opSurvey.get();

            QAvcEvent avcEvent = QAvcEvent.avcEvent;
            JPAQuery<Tuple> query = new JPAQuery<>(entityManager);

            query.select(
                    avcEvent.type,
                    avcEvent.count()
            )
                    .from(avcEvent)
                    .where(avcEvent.survey.eq(survey))
                    .groupBy(avcEvent.type);
            List<Tuple> tuples = query.fetch();
            List<AvcSurveyReport> avcSurveyReport = new ArrayList<>();
            for (Tuple tuple : tuples) avcSurveyReport.add(new AvcSurveyReport(tuple));

            Path path = Paths.get(uploadDirPath);
            String filename= path.resolve(UUID.randomUUID().toString() + ".xlsx").toString();
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(file);

            SXSSFWorkbook workbook = new SXSSFWorkbook();
            createStyle(workbook);
            int rowNumber = 0, colNumber = 0;
            Sheet avcSurveySheet = workbook.createSheet("avc survey");
            Row row = avcSurveySheet.createRow(rowNumber++);
            row.createCell(colNumber++, Cell.CELL_TYPE_STRING).setCellValue("Survey : ");
            row.createCell(colNumber++, Cell.CELL_TYPE_STRING).setCellValue(survey.getName());
            row.createCell(colNumber++, Cell.CELL_TYPE_STRING).
                    setCellValue("(" + survey.getStartDate() + " - " + survey.getEndDate() + ")");
            colNumber = 0;

            row = avcSurveySheet.createRow(rowNumber++);
            row.createCell(colNumber++, Cell.CELL_TYPE_STRING).setCellValue("Event Type");
            row.createCell(colNumber++, Cell.CELL_TYPE_STRING).setCellValue("Event Count");

            for (AvcSurveyReport record : avcSurveyReport) {
                colNumber = 0;
                row = avcSurveySheet.createRow(rowNumber++);
                row.createCell(colNumber++, Cell.CELL_TYPE_STRING).setCellValue(record.getType());
                row.createCell(colNumber++, Cell.CELL_TYPE_STRING).setCellValue(record.getCount());
            }

            workbook.write(outputStream);
            workbook.dispose();
            return filename;
        } else {
            throw new NotFoundException("Can't find survey with id(unmasked) : " + surveyId);
        }
    }
}
