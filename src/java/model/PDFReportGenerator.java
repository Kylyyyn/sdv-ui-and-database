package model;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class PDFReportGenerator {

    private static final BaseColor NAVY_HEADER = new BaseColor(74, 68, 107);
    private static final BaseColor HEADER_TEXT = new BaseColor(255, 248, 220);
    private static final BaseColor FOOTER_BG   = new BaseColor(100, 90, 130);
    private static final BaseColor PAGE_BG     = new BaseColor(250, 246, 235);
    private static final BaseColor ROW_EVEN    = new BaseColor(245, 238, 220);
    private static final BaseColor ROW_ODD     = new BaseColor(255, 252, 242);
    private static final BaseColor BORDER_COL  = new BaseColor(180, 165, 140);
    private static final BaseColor DATA_TEXT   = new BaseColor(60, 50, 70);

    public static byte[] generateAdminReport(List<User> users,
            String owner, String timestamp) throws Exception {

        ByteArrayOutputStream pass1 = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.LETTER, 36, 36, 60, 60);
        PdfWriter writer = PdfWriter.getInstance(doc, pass1);
        PageCountEvent counter = new PageCountEvent();
        writer.setPageEvent(new ReportPageEvent("Admin Report", owner, timestamp, counter));
        doc.open();
        doc.add(buildAdminTable(users));
        doc.close();

        return stampTotalPages(pass1, counter.totalPages);
    }

    public static byte[] generateGuestReport(User user,
            String decryptedPassword, String timestamp) throws Exception {

        ByteArrayOutputStream pass1 = new ByteArrayOutputStream();
        Rectangle customSize = new Rectangle(595f, 500f);
        Document doc = new Document(customSize, 36, 36, 60, 60);
        PdfWriter writer = PdfWriter.getInstance(doc, pass1);
        PageCountEvent counter = new PageCountEvent();
        writer.setPageEvent(new ReportPageEvent("Guest Report", user.getEmail(), timestamp, counter));

        doc.open();

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(90);
        table.setSpacingBefore(10f);

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, HEADER_TEXT);
        for (String col : new String[]{"Username", "Password"}) {
            PdfPCell cell = new PdfPCell(new Phrase(col, headerFont));
            cell.setBackgroundColor(NAVY_HEADER);
            cell.setPadding(8f);
            cell.setBorderColor(BORDER_COL);
            cell.setBorderWidth(0.5f);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10, DATA_TEXT);
        for (String val : new String[]{user.getEmail(), decryptedPassword}) {
            PdfPCell cell = new PdfPCell(new Phrase(val, dataFont));
            cell.setBackgroundColor(ROW_EVEN);
            cell.setPadding(6f);
            cell.setBorderColor(BORDER_COL);
            cell.setBorderWidth(0.5f);
            table.addCell(cell);
        }

        doc.add(table);
        doc.close();

        return stampTotalPages(pass1, counter.totalPages);
    }

    private static byte[] stampTotalPages(ByteArrayOutputStream pass1, int totalPages)
            throws Exception {

        ByteArrayOutputStream pass2 = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(pass1.toByteArray());
        PdfStamper stamper = new PdfStamper(reader, pass2);

        BaseFont bf = BaseFont.createFont(
                BaseFont.HELVETICA_OBLIQUE, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);

        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            Rectangle pageSize = reader.getPageSize(i);
            float right = pageSize.getWidth() - 36; 

            PdfContentByte cb = stamper.getOverContent(i);
            cb.beginText();
            cb.setFontAndSize(bf, 9);
            cb.setColorFill(HEADER_TEXT);
            String total = " " + totalPages;
            float totalWidth = bf.getWidthPoint(total, 9);
            float gap = 3f; 
cb.setTextMatrix(right - totalWidth + gap, 10);
            cb.showText(total);
            cb.endText();
        }

        stamper.close();
        reader.close();
        return pass2.toByteArray();
    }

    private static PdfPTable buildAdminTable(List<User> users) throws DocumentException {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(90);
        table.setSpacingBefore(10f);
        table.setWidths(new float[]{3f, 1f});

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, HEADER_TEXT);
        for (String h : new String[]{"Username", "Role"}) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(NAVY_HEADER);
            cell.setPadding(8f);
            cell.setBorderColor(BORDER_COL);
            cell.setBorderWidth(0.5f);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10, DATA_TEXT);
        int rowNum = 0;
        for (User u : users) {
            BaseColor rowBg = (rowNum % 2 == 0) ? ROW_EVEN : ROW_ODD;

            PdfPCell emailCell = new PdfPCell(new Phrase(u.getEmail(), dataFont));
            emailCell.setBackgroundColor(rowBg);
            emailCell.setPadding(6f);
            emailCell.setBorderColor(BORDER_COL);
            emailCell.setBorderWidth(0.5f);
            table.addCell(emailCell);

            PdfPCell roleCell = new PdfPCell(new Phrase(u.getRole(), dataFont));
            roleCell.setBackgroundColor(rowBg);
            roleCell.setPadding(6f);
            roleCell.setBorderColor(BORDER_COL);
            roleCell.setBorderWidth(0.5f);
            roleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(roleCell);

            rowNum++;
        }
        return table;
    }

    static class PageCountEvent extends PdfPageEventHelper {
        int totalPages = 0;

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            totalPages = writer.getPageNumber();
        }
    }

    static class ReportPageEvent extends PdfPageEventHelper {

        private final String reportType;
        private final String owner;
        private final String timestamp;
        private final PageCountEvent counter;

        ReportPageEvent(String reportType, String owner,
                        String timestamp, PageCountEvent counter) {
            this.reportType = reportType;
            this.owner      = owner;
            this.timestamp  = timestamp;
            this.counter    = counter;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            // Update counter so pass 2 knows the total
            counter.totalPages = writer.getPageNumber();

            PdfContentByte cbUnder = writer.getDirectContentUnder();
            PdfContentByte cb      = writer.getDirectContent();

            Rectangle pageSize = document.getPageSize();
            float left  = document.leftMargin();
            float right = pageSize.getWidth() - document.rightMargin();

            // Page background — behind content
            cbUnder.setColorFill(PAGE_BG);
            cbUnder.rectangle(0, 0, pageSize.getWidth(), pageSize.getHeight());
            cbUnder.fill();

            // Top header banner
            cb.setColorFill(NAVY_HEADER);
            cb.rectangle(0, pageSize.getHeight() - 50, pageSize.getWidth(), 50);
            cb.fill();

            // Bottom footer bar
            cb.setColorFill(FOOTER_BG);
            cb.rectangle(0, 0, pageSize.getWidth(), 28);
            cb.fill();

            // Report type — bold, top center
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, HEADER_TEXT);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    new Phrase(reportType, titleFont),
                    pageSize.getWidth() / 2, pageSize.getHeight() - 30, 0);

            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, HEADER_TEXT);

            // Owner — bottom left
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                    new Phrase("Owner: " + owner, footerFont),
                    left, 10, 0);

            // Timestamp — bottom center
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    new Phrase(timestamp, footerFont),
                    pageSize.getWidth() / 2, 10, 0);

            // "Page X of " — bottom right (total stamped in pass 2 right after this)
            try {
                BaseFont bf = BaseFont.createFont(
                        BaseFont.HELVETICA_OBLIQUE, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
                String pageText = "Page " + writer.getPageNumber() + " of ";
                float textWidth = bf.getWidthPoint(pageText, 9);

                cb.beginText();
                cb.setFontAndSize(bf, 9);
                cb.setColorFill(HEADER_TEXT);
                cb.setTextMatrix(right - textWidth, 10);
                cb.showText(pageText);
                cb.endText();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}