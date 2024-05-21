package com.mirai.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mirai.exception.ApplicationErrorCode;
import com.mirai.exception.MiraiException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Slf4j
public class MiraiUtils {
    /**
     * Creates a pageable object based on the provided limit and offset values.
     *
     * @param limit  Maximum number of records to retrieve.
     * @param offset Number of records to skip from the beginning.
     * @return Pageable object for pagination.
     * @throws  MiraiException limit or offset is less than zero.
     */
    public static Pageable createPageable(String limit, String offset) {
        log.info("Start of createPageable method. Creating Pageable object with limit: {}, offset: {}", limit, offset);
        int limitValue = limit == null || limit.isBlank() || limit.equals("0") ? 5 : Integer.parseInt(limit);
        int offsetValue = offset == null || offset.isBlank() || offset.equals("0")
                ? 0
                : Integer.parseInt(offset); // || Integer.parseInt(offset) < 0

        if (limitValue == 0) limitValue = 5;

        if (limitValue < 0 || offsetValue < 0) {
            throw new MiraiException(ApplicationErrorCode.LIMIT_OFFSET_NOT_VALID);
        }
        log.info(
                "End of createPageable method. Created Pageable object with limit: {}, offset: {}",
                limitValue,
                offsetValue);
        return PageRequest.of(offsetValue, limitValue);
    }

    /**
     * Generates a QR code image as a byte array.
     *
     * @param text   The text or URL to be encoded in the QR code.
     * @param width  The width of the QR code image.
     * @param height The height of the QR code image.
     * @return A byte array representing the generated QR code image.
     * @throws WriterException If an error occurs while generating the QR code.
     * @throws IOException     If an I/O error occurs while writing the image to the byte array.
     */
    public static byte[] generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        log.info("Generating QR code image for text: {}", text);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "JPEG", outputStream);
        log.info("QR code image generated successfully");
        return outputStream.toByteArray();
    }

    //    public static byte[] generateQRCodeImage(String text, int width, int height) throws WriterException,
    // IOException {
    //        log.info("Generating QR code image for text: {}", text);
    //        QRCodeWriter qrCodeWriter = new QRCodeWriter();
    //        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
    //
    //        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
    //        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    //        try {
    //            ImageIO.write(qrImage, "PNG", outputStream);
    //            log.info("QR code image generated successfully");
    //        } catch (IOException e) {
    //            log.error("Could not generate QR code image", e);
    //        }
    //        return outputStream.toByteArray();
    //    }
}
