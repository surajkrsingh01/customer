package com.shoppurs.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.text.Html;
import android.util.Log;
import android.util.Printer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.shoppurs.R;
import com.shoppurs.adapters.InvoiceItemAdapter;
import com.shoppurs.models.InvoiceItem;
import com.shoppurs.utilities.ConnectionDetector;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;
import com.shoppurs.utilities.EnglishNumberToWords;
import com.shoppurs.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class InvoiceActivity extends NetworkBaseActivity {

    private final int SHARE =1,PRINT =2,DOWNLOAD=3;

    private RecyclerView recyclerView;
    private List<InvoiceItem> itemList;
    private InvoiceItemAdapter itemAdapter;

    private TextView tvShopName,tvShopAddress,tvShopEmail,tvShopPhone,tvShopGSTIN,tvInvoiceNo,tvDate,tvCustomerName,
                      textCustomerMobile,textBarcodeNo,
                      tvSubTotAmt,tvGrossTotAmt,tvTotSgst,tvTotCgst,tvTotIgst,textTotalAmount,tvDiscountedItems,
                      textBaseCgstAmount,textBaseSgstAmount,textBaseIgstAmount,
                      tvPaidAmt,tvTotQty,tvDiscount,tvPaymentMethod,tvPaymentBrand,tvTransId,tvPaymentAmount,textDeliveryAmount,tvTotSavings;
    private LinearLayout llFeedback;
    private EditText etFeedback;
    private RatingBar ratingBar;

    private ImageView image_barcode;
    private RelativeLayout rlCouponLayout;
    private TextView tvCouponOfferName;
    private int actionType,disItems;

    private boolean customerCopy = true;
    private Printer printer;
    private File pdfFile;
    private List<Bitmap> bitmaps;
    private Bitmap barcodeImage;
    private String custCode,transId,invoiceNo,netPayableAmt,netPayableWords,shortExcess;

    private float totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

        itemList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        itemAdapter=new InvoiceItemAdapter(this,itemList);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setNestedScrollingEnabled(false);

      //  getItemList();

        if(ConnectionDetector.isNetworkAvailable(this))
        getInvoice();

    }

    private void init(){
        tvShopName = findViewById(R.id.text_shop_name);
        tvShopAddress = findViewById(R.id.text_shop_address);
        tvShopEmail = findViewById(R.id.text_shop_email);
        tvShopPhone = findViewById(R.id.text_shop_phone);
        tvShopGSTIN = findViewById(R.id.text_shop_gst);
        tvInvoiceNo = findViewById(R.id.text_invoice_no);
        tvDate = findViewById(R.id.text_date);
        tvCustomerName = findViewById(R.id.text_customer_name);
        textCustomerMobile = findViewById(R.id.textCustomerMobile);
        tvSubTotAmt = findViewById(R.id.text_sub_total_amount);
        textDeliveryAmount = findViewById(R.id.textDeliveryAmount);
        tvGrossTotAmt = findViewById(R.id.text_gross_total_amount);
        tvTotSgst = findViewById(R.id.text_sgst_tax_amt);
        tvTotCgst = findViewById(R.id.text_cgst_tax_amt);
        tvTotIgst = findViewById(R.id.text_igst_tax_amt);
        textTotalAmount = findViewById(R.id.textTotalAmount);
        tvPaidAmt = findViewById(R.id.textPaidAmount);
        tvDiscount = findViewById(R.id.text_total_savings);
        tvDiscountedItems = findViewById(R.id.tvDiscountedItems);
        tvPaymentMethod = findViewById(R.id.text_payment_method);
        tvPaymentBrand = findViewById(R.id.text_payment_brand);
        tvTransId = findViewById(R.id.text_payment_transaction_id);
        tvTotSavings = findViewById(R.id.text_total_savings);
        tvTotQty = findViewById(R.id.text_total_items);
        image_barcode = findViewById(R.id.image_barcode);
        textBaseCgstAmount = findViewById(R.id.textBaseCgstAmount);
        textBaseSgstAmount = findViewById(R.id.textBaseSgstAmount);
        textBaseIgstAmount = findViewById(R.id.textBaseIgstAmount);
        textBarcodeNo = findViewById(R.id.textBarcodeNo);
        ratingBar = findViewById(R.id.ratingBar);
        Button btn_submit = findViewById(R.id.btn_submit);

        TextView tvDearCustomerInfo = findViewById(R.id.tvDearCustomerInfo);
        String dearCustomer = "<p>Dear Customer<br/>Thank you for shopping at Shoppurs.<br/>In case you would like to Exchange" +
                " any of the articles purchased, we request you to carry your bill along. It will be our pleasure to serve you again.</p>";
        tvDearCustomerInfo.setText(Html.fromHtml(dearCustomer));

        rlCouponLayout = findViewById(R.id.rl_coupon_layout);
        tvCouponOfferName = findViewById(R.id.tv_offer_name);

        ImageView ivClose = findViewById(R.id.image_close);
        ImageView ivShare = findViewById(R.id.image_share);
        ImageView ivPrint = findViewById(R.id.image_print);
        ImageView ivDownload = findViewById(R.id.image_download);

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionType = SHARE;
                createNewPdf();
            }
        });

        ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionType = DOWNLOAD;
                createNewPdf();
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void getInvoice(){
        Map<String,String> params=new HashMap<>();
        params.put("number",getIntent().getStringExtra("orderNumber"));
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        String url=getResources().getString(R.string.url_shop)+Constants.GET_INVOICE;
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"orders");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {

        try {
            if (apiName.equals("orders")) {
                if (response.getBoolean("status")) {
                   // JSONArray dataArray = response.getJSONArray("result");
                    JSONObject jsonObject = response.getJSONObject("result");
                    JSONArray invoiceDetailsArray = jsonObject.getJSONArray("invoiceDetailList");
                    tvShopName.setText(jsonObject.getString("invShopName"));
                    tvShopAddress.setText(jsonObject.getString("invShopAddress"));
                    tvShopPhone.setText("Ph: "+jsonObject.getString("invShopMobile"));
                    tvShopEmail.setText("Email: "+jsonObject.getString("invShopEmail"));
                    tvShopGSTIN.setText("GSTIN: "+jsonObject.getString("invShopGSTIn"));
                    tvDate.setText(jsonObject.getString("invDate"));
                    tvCustomerName.setText(jsonObject.getString("invCustName"));
                    custCode = jsonObject.getString("custCode");
                    if(jsonObject.has("invCustMobile"))
                    textCustomerMobile.setText(jsonObject.getString("invCustMobile"));
                    else{
                        textCustomerMobile.setVisibility(View.GONE);
                    }
                    tvInvoiceNo.setText("Tax Invoice No: "+jsonObject.getString("invNo"));
                    invoiceNo = jsonObject.getString("invNo");
                    textBarcodeNo.setText(invoiceNo);
                    generateBarcode(invoiceNo);
                    float subTotal = Float.parseFloat(""+(jsonObject.getDouble("invTotAmount") - jsonObject.getDouble("invTotTaxAmount")));
                    tvSubTotAmt.setText(Utility.numberFormat(subTotal));
                    tvGrossTotAmt.setText(Utility.numberFormat(subTotal));
                    tvTotIgst.setText(Utility.numberFormat(jsonObject.getDouble("invTotIGST")));
                    tvTotSgst.setText(Utility.numberFormat(jsonObject.getDouble("invTotSGST")));
                    tvTotCgst.setText(Utility.numberFormat(jsonObject.getDouble("invTotCGST")));
                    shortExcess = Utility.numberFormat(jsonObject.getDouble("invTotDisAmount"));
                   // tvShortExcess.setText(Utility.numberFormat(jsonObject.getDouble("invTotDisAmount")));
                    float netPayable = (float) Math.round(jsonObject.getDouble("invTotNetPayable"));
                    netPayableAmt = Utility.numberFormat(netPayable);
                    //tvNetPayableAmt.setText(Utility.numberFormat(netPayable));
                   // tvShortExcess.setText(Utility.numberFormat(netPayable - (float)jsonObject.getDouble("invTotNetPayable")));
                    tvPaidAmt.setText(Utility.numberFormat(netPayable));
                    netPayableWords = EnglishNumberToWords.convert((int)netPayable)+" rupees";
                 //   tvNetPayableWords.setText(EnglishNumberToWords.convert((int)netPayable)+" rupees");
                    tvPaymentMethod.setText(jsonObject.getString("paymentMethod"));
                    tvPaymentBrand.setText(jsonObject.getString("paymentBrand"));
                    tvTransId.setText(jsonObject.getString("invTransId"));
                    transId = jsonObject.getString("invTransId");
                   // tvPaymentAmount.setText(Utility.numberFormat(netPayable));
                    tvTotSavings.setText(Utility.numberFormat(jsonObject.getDouble("invTotDisAmount")));
                    tvDiscount.setText(Utility.numberFormat(jsonObject.getDouble("invTotDisAmount")));
                    String deliveryMode = jsonObject.getString("deliveryMode");

                   // totDiscount = (float) jsonObject.getDouble("invTotDisAmount");
                   // tvDiscount.setText("-"+Utility.numberFormat(totDiscount));
                   /* int couponId = jsonObject.getInt("invCoupenId");
                    if(couponId == 0){
                        rlCouponLayout.setVisibility(View.GONE);
                    }else{
                        rlCouponLayout.setVisibility(View.VISIBLE);
                        Coupon coupon = dbHelper.getCouponOffer(String.valueOf(couponId));
                        tvCouponOfferName.setText(coupon.getName());
                    }*/

                    int len = invoiceDetailsArray.length();
                  //  InvoiceDetail invoiceDetail= null;
                    int totQty = 0;
                    float totSp = 0;
                    for (int i = 0; i < len; i++) {
                        jsonObject = invoiceDetailsArray.getJSONObject(i);
                        //invoiceDetail = new InvoiceDetail();

                        InvoiceItem item = new InvoiceItem();
                        item.setItemName(jsonObject.getString("invDProdName"));
                        item.setHsn(jsonObject.getString("invDHsnCode"));
                        item.setQty(jsonObject.getInt("invDQty"));
                        item.setCgst(Float.parseFloat(jsonObject.getString("invDCGST")));
                        item.setSgst(Float.parseFloat(jsonObject.getString("invDSGST")));
                        item.setIgst(Float.parseFloat(jsonObject.getString("invDIGST")));
                        item.setMrp((float) jsonObject.getDouble("invDMrp"));
                        item.setDisAmt((float) jsonObject.getDouble("invDDisAmount"));
                        if(item.getDisAmt() > 0){
                            disItems = disItems + 1;
                        }
                        item.setSp((float) jsonObject.getDouble("invDSp"));
                        float rate = Float.parseFloat(jsonObject.getString("invDSp"));
                        float cgst = Float.parseFloat(jsonObject.getString("invDCGST"));
                        float sgst = Float.parseFloat(jsonObject.getString("invDSGST"));
                        rate = rate - ((rate * (cgst+sgst))/(100 + (cgst+sgst)));
                        item.setRate(rate);
                        totalAmount = totalAmount + (rate * item.getQty());
                        totSp = totSp + (item.getQty() * item.getSp());
                        item.setFreeItems(jsonObject.getInt("invDFreeItems"));
                        item.setOfferId(jsonObject.getString("invdOfferId"));
                        item.setOfferType(jsonObject.getString("invdOfferType"));
                        item.setUnit(jsonObject.getString("invdProdUnit"));
                        item.setColor(jsonObject.getString("invdProdColor"));
                        item.setSize(jsonObject.getString("invdProdSize"));
                        itemList.add(item);
                        totQty = totQty + item.getQty();
                    }

                    tvSubTotAmt.setText(Utility.numberFormat(totalAmount));
                    tvGrossTotAmt.setText(Utility.numberFormat(netPayable));
                    textTotalAmount.setText(Utility.numberFormat(netPayable));
                    textBaseCgstAmount.setText(Utility.numberFormat(totalAmount));
                    textBaseSgstAmount.setText(Utility.numberFormat(totalAmount));
                    textBaseIgstAmount.setText(Utility.numberFormat(totalAmount));
                    if(deliveryMode.equals("home")){
                        float deliveryCharges = netPayable-totSp;
                        if(deliveryCharges > 0){
                            findViewById(R.id.rlDelivery).setVisibility(View.VISIBLE);
                            findViewById(R.id.separator_delivery).setVisibility(View.VISIBLE);
                            textDeliveryAmount.setText(Utility.numberFormat(deliveryCharges));
                        }
                    }

                    tvTotQty.setText(""+totQty);
                    tvDiscountedItems.setText("Discounted Items: "+disItems);
                    itemAdapter.notifyDataSetChanged();

                    if(jsonObject.getString("invShopRemarks").equals("null")){
                        llFeedback.setVisibility(View.GONE);
                    }

                }
            }if (apiName.equals("submitFeedback")) {
                if (response.getBoolean("status")) {
                    llFeedback.setVisibility(View.GONE);
                    DialogAndToast.showToast(response.getString("message"),this);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getItemList(){
        InvoiceItem item = new InvoiceItem();
        item.setItemName("Ghee Pongal");
        item.setHsn("9963");
        item.setQty(1);
        item.setGst(5);
        item.setRate(50);
        itemList.add(item);

        item = new InvoiceItem();
        item.setItemName("Coffee");
        item.setHsn("9963");
        item.setQty(1);
        item.setGst(5);
        item.setRate(25);
        itemList.add(item);

        itemAdapter.notifyDataSetChanged();
    }

    public String getFile() {
       // String date=new SimpleDateFormat("yyyy_MM_dd").format(new Date());
       // String time=new SimpleDateFormat("HH:mm:ss").format(new Date());

        String root="";
        if(BaseImageActivity.isExternalStorageAvailable()){
            root = Environment.getExternalStoragePublicDirectory("").toString();
        }
        File myDir = new File(root+"/Shoppurs/Shoppurs documents");
        myDir.mkdirs();
        String fname="Invoice"+invoiceNo+".pdf";
       // fname = date+time+".pdf";

        String imagePath=root+"/Shoppurs/Shoppurs documents/"+fname;
       // File file = new File (myDir, fname);
        return imagePath;

    }

    private void createNewPdf(){
        if(Utility.verifyStorageOnlyPermissions(this)) {
            String fileName = getFile();
            File file = new File(fileName);
            if(file.exists()){
                Log.i(TAG,"pdf file is already exist.");
                if(actionType == SHARE){
                    share(fileName);
                }/*else if(actionType == PRINT){
                    print(fileName);
                }*/if(actionType == DOWNLOAD){
                    download(fileName);
                }
                //DialogAndToast.showToast("Pdf downloaded successfully. The location is "+fileName,this);
                return;
            }

            /***
             * Variables for further use....
             */
            BaseColor baseOragnge = new BaseColor(30, 136, 229, 255);
            BaseColor baseLightOragnge = new BaseColor(227, 242, 253, 255);
            BaseColor baseLightBlue  = new BaseColor(245, 245, 245, 255);
            BaseColor baseVeryLightBlue = new BaseColor(250, 250, 250, 255);
            BaseColor baseBlue  = new BaseColor(30, 136, 229, 255);
            float mHeadingFontSize = 22.0f;
            float mSubHeadingFontSize = 18.0f;
            float mValueFontSize = 15.0f;
            float spacing = 20;
            float cellPadding = 15;

            BaseFont baseFont = null;

            /**
             * How to USE FONT....
             */
            try {
                baseFont = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
            //lineSeparator.setOffset(-8);

            // create a new document
            //PdfDocument document = new PdfDocument();
            Document document = new Document();
            // Location to save
            try {
                PdfWriter.getInstance(document, new FileOutputStream(fileName));
                // Open to write
                document.open();
                // Document Settings
                document.setPageSize(PageSize.A4);
                document.addCreationDate();
                document.addAuthor("Shoppurs");
                document.addCreator(sharedPreferences.getString(Constants.FULL_NAME, ""));

                // Title Order Details...
// Adding Title....
                Font headerFont = new Font(baseFont, mHeadingFontSize, Font.NORMAL, BaseColor.BLACK);
                Font headerOrangeFont = new Font(baseFont, mHeadingFontSize, Font.NORMAL, baseOragnge);
                Font descFont = new Font(baseFont, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
                Font descDarkGrayFont = new Font(baseFont, mValueFontSize, Font.NORMAL, BaseColor.DARK_GRAY);
                Font descGrayFont = new Font(baseFont, mValueFontSize, Font.NORMAL, BaseColor.GRAY);
                Font descBlueFont = new Font(baseFont, mValueFontSize, Font.NORMAL, baseBlue);
                Font subHeaderOrangeFont = new Font(baseFont, mSubHeadingFontSize, Font.NORMAL, baseOragnge);

                Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.pdf_logo);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                logo.compress(Bitmap.CompressFormat.JPEG, 100 , stream);
                Image myImg = Image.getInstance(stream.toByteArray());
                myImg.setAlignment(Image.MIDDLE);
               // document.add(myImg);

                PdfPTable pdfImageTable = new PdfPTable(1);
                pdfImageTable.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfImageTable.setWidthPercentage(100);

                PdfPCell pdfPCell = new PdfPCell();
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setBorder(Rectangle.NO_BORDER);
                pdfPCell.setPaddingLeft(30);
                pdfPCell.setPaddingRight(30);
                pdfPCell.addElement(myImg);
                pdfImageTable.addCell(pdfPCell);
                document.add(pdfImageTable);

                Chunk chunkVerticalMark = new Chunk(new VerticalPositionMark());
                Chunk chunk2 = new Chunk(tvDate.getText().toString(), descFont);
                Chunk chunk1 = new Chunk("Purchase Details", headerOrangeFont);
                Paragraph dateParagraph1 = new Paragraph(chunk1);
                dateParagraph1.add(new Chunk(chunkVerticalMark));
                dateParagraph1.add(chunk2);
                dateParagraph1.setSpacingAfter(10);
                document.add(dateParagraph1);


                document.add(lineSeparator);

                Chunk chunk3 = new Chunk("Transaction id:   ", descFont);
                Chunk chunk4 = new Chunk(transId, descGrayFont);
                Paragraph dateParagraph2 = new Paragraph(chunk3);
                dateParagraph2.add(chunk4);
               // dateParagraph2.setSpacingBefore(spacing);
                document.add(dateParagraph2);

                PdfPTable pdfPTable = new PdfPTable(31);
                pdfPTable.setHorizontalAlignment(Element.ALIGN_UNDEFINED);
                pdfPTable.setWidthPercentage(100);
                pdfPTable.setSpacingBefore(spacing);
                pdfPTable.setSpacingAfter(spacing);
                //pdfPTable.set

                Paragraph dateParagraph3 = new Paragraph();
                Paragraph dateParagraph4 = new Paragraph();
                Chunk chunk5 = new Chunk("Purchase Summary", subHeaderOrangeFont);
                Chunk chunk6 = new Chunk("Total Amount", descDarkGrayFont);
                Chunk chunk8 = new Chunk("Items", descDarkGrayFont);
                dateParagraph3.add(chunk5);
                dateParagraph4.add(chunk6);
                dateParagraph4.add(chunkVerticalMark);
                dateParagraph4.add(chunk8);
                dateParagraph4.setSpacingBefore(20);
                Paragraph dateParagraph5 = new Paragraph();
                Chunk chunk9 = new Chunk(textTotalAmount.getText().toString(), descFont);
                Chunk chunk10 = new Chunk(tvTotQty.getText().toString(), descFont);
                dateParagraph5.add(chunk9);
                dateParagraph5.add(chunkVerticalMark);
                dateParagraph5.add(chunk10);

                Paragraph dateParagraph6 = new Paragraph();
                Chunk chunk11 = new Chunk(tvDiscountedItems.getText().toString(), descDarkGrayFont);
                dateParagraph6.setSpacingBefore(20);
                dateParagraph6.add(chunk11);

                Paragraph dateParagraph7 = new Paragraph();
                Chunk chunk12 = new Chunk("Total Savings", descDarkGrayFont);
                dateParagraph7.add(chunk12);

                Paragraph dateParagraph8 = new Paragraph();
                dateParagraph8.setSpacingBefore(5);
                Chunk chunk13 = new Chunk(tvDiscount.getText().toString(), subHeaderOrangeFont);
                dateParagraph8.add(chunk13);

                PdfPCell itemNameLabelCell = new PdfPCell();
                itemNameLabelCell.setUseAscender(true);
                itemNameLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                itemNameLabelCell.setVerticalAlignment(Element.ALIGN_TOP);
                itemNameLabelCell.setBorder(Rectangle.NO_BORDER);
                itemNameLabelCell.addElement(dateParagraph3);
                itemNameLabelCell.addElement(dateParagraph4);
                itemNameLabelCell.addElement(dateParagraph5);
                itemNameLabelCell.addElement(dateParagraph6);
                itemNameLabelCell.addElement(dateParagraph7);
                itemNameLabelCell.addElement(dateParagraph8);
                itemNameLabelCell.setBackgroundColor(baseLightOragnge);
                itemNameLabelCell.setPadding(20);
                itemNameLabelCell.setColspan(15);
                pdfPTable.addCell(itemNameLabelCell);

                PdfPCell cellBlank = new PdfPCell();
                cellBlank.setBorder(Rectangle.NO_BORDER);
                cellBlank.setColspan(1);
                pdfPTable.addCell(cellBlank);

                PdfPCell cell2 = new PdfPCell();
                Paragraph dateParagraph9 = new Paragraph();
                Chunk chunk7 = new Chunk("Payment Details", subHeaderOrangeFont);
                dateParagraph9.setSpacingAfter(5);
                dateParagraph9.add(chunk7);

                Paragraph dateParagraph10 = new Paragraph();
                Chunk chunk14 = new Chunk("Payment Method", descDarkGrayFont);
                Chunk chunk15 = new Chunk(tvPaymentMethod.getText().toString(), descDarkGrayFont);
                dateParagraph10.add(chunk14);
                dateParagraph10.add(chunkVerticalMark);
                dateParagraph10.add(chunk15);
                dateParagraph10.setSpacingBefore(15);

                Paragraph dateParagraph11 = new Paragraph();
                Chunk chunk16 = new Chunk("Payment Brand", descDarkGrayFont);
                Chunk chunk17 = new Chunk(tvPaymentBrand.getText().toString(), descDarkGrayFont);
                dateParagraph11.add(chunk16);
                dateParagraph11.add(chunkVerticalMark);
                dateParagraph11.add(chunk17);
                dateParagraph11.setSpacingAfter(50);

                Paragraph dateParagraph12 = new Paragraph();
                Chunk chunk18 = new Chunk("Amount", descDarkGrayFont);
                Chunk chunk19 = new Chunk(textTotalAmount.getText().toString(), descFont);
                dateParagraph12.add(chunk18);
                dateParagraph12.add(chunkVerticalMark);
                dateParagraph12.add(chunk19);
                dateParagraph12.setSpacingBefore(10);

                cell2.setUseAscender(true);
                cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell2.setVerticalAlignment(Element.ALIGN_TOP);
                cell2.setBorder(Rectangle.NO_BORDER);
                cell2.addElement(dateParagraph9);
                cell2.addElement(lineSeparator);
                cell2.addElement(dateParagraph10);
                cell2.addElement(dateParagraph11);
                cell2.addElement(lineSeparator);
                cell2.addElement(dateParagraph12);
                cell2.setBackgroundColor(baseLightBlue);
                cell2.setPadding(20);
                cell2.setColspan(15);
                pdfPTable.addCell(cell2);

                document.add(pdfPTable);

                PdfPTable pdfPTable2 = new PdfPTable(6);
                pdfPTable2.setWidthPercentage(100);
                pdfPTable2.setSpacingAfter(10);

                String[] topHeader = {"Item Name","Qty","Net Amt"};
                Chunk chunk20 = null;
                PdfPCell cell3 = null;
                for(String header: topHeader){
                    cell3 = new PdfPCell();
                    cell3.setPadding(cellPadding);
                    cell3.setVerticalAlignment(Element.ALIGN_TOP);
                    cell3.setBackgroundColor(baseLightBlue);
                    cell3.setBorder(Rectangle.TOP);

                    if(header.equals("Item Name"))
                    cell3.setColspan(4);

                    chunk20 = new Chunk(header,descFont);
                    cell3.addElement(chunk20);
                    pdfPTable2.addCell(cell3);
                }

                String[] belowHeader = {"HSN","Disc Amt","MRP","CGST%","SGST%","Tax Amt"};
                for(String header: belowHeader){
                    cell3 = new PdfPCell();
                    cell3.setPadding(cellPadding);
                    cell3.setVerticalAlignment(Element.ALIGN_TOP);
                    cell3.setBackgroundColor(baseLightBlue);
                    cell3.setBorder(Rectangle.TOP);
                    chunk20 = new Chunk(header,descFont);
                    cell3.addElement(chunk20);
                    pdfPTable2.addCell(cell3);
                }
                int position = 0;
                for(InvoiceItem item : itemList){
                    String name = item.getItemName();
                    if(item.getFreeItems() > 0){
                        if(item.getOfferType().equals("free")){
                            if(item.getFreeItems() == 1){
                                name = item.getItemName()+" ("+item.getFreeItems()+" free item)";
                            }else{
                                name = item.getItemName()+" ("+item.getFreeItems()+" free items)";
                            }

                        }
                    }

                    for(String header: topHeader){
                        cell3 = new PdfPCell();
                        cell3.setPaddingLeft(cellPadding);
                        cell3.setPaddingTop(cellPadding);
                        cell3.setPaddingRight(cellPadding);
                        cell3.setVerticalAlignment(Element.ALIGN_TOP);
                        if(position % 2 == 0)
                        cell3.setBackgroundColor(baseVeryLightBlue);

                        if(position == 0)
                        cell3.setBorder(Rectangle.TOP);
                        else{
                            cell3.setBorder(Rectangle.NO_BORDER);
                        }

                        chunk20 = new Chunk();
                        if(header.equals("Item Name")){
                            chunk20.append(name);
                            cell3.setColspan(4);
                        }else if(header.equals("Qty")){
                            String qty = "";
                            if(item.getUnit() == null || item.getUnit().toLowerCase().equals("null") || item.getUnit().equals("")){
                                qty = ""+item.getQty();
                            }else{
                                String[] unitArray = item.getUnit().split(" ");
                                float totalUnit = Float.parseFloat(unitArray[0]) * item.getQty();
                                qty = String.format("%.00f",totalUnit)+" "+unitArray[1];
                            }
                            chunk20.append(qty);
                        }else if(header.equals("Net Amt")){
                            float amt = item.getQty() * item.getRate();
                            chunk20.append(Utility.numberFormat(amt));
                        }
                        chunk20.setFont(descGrayFont);
                        cell3.addElement(chunk20);
                        pdfPTable2.addCell(cell3);
                    }

                    for(String header: belowHeader){
                        cell3 = new PdfPCell();
                        cell3.setPaddingLeft(cellPadding);
                        cell3.setPaddingRight(cellPadding);
                        if(position == itemList.size()-1){
                            cell3.setPaddingBottom(cellPadding);
                        }
                        cell3.setBorder(Rectangle.NO_BORDER);
                        cell3.setVerticalAlignment(Element.ALIGN_TOP);
                        if(position % 2 == 0)
                            cell3.setBackgroundColor(baseVeryLightBlue);

                        chunk20 = new Chunk();
                        if(header.equals("HSN")){
                            chunk20.append(item.getHsn());
                        }else if(header.equals("Disc Amt")){
                            chunk20.append(Utility.numberFormat(item.getDisAmt()));
                        }else if(header.equals("MRP")){
                            chunk20.append(Utility.numberFormat(item.getMrp()));
                        }else if(header.equals("CGST%")){
                            chunk20.append(String.format("%.02f",item.getCgst()));
                        }else if(header.equals("SGST%")){
                            chunk20.append(String.format("%.02f",item.getSgst()));
                        }else if(header.equals("Tax Amt")){
                            chunk20.append(Utility.numberFormat(item.getQty() * (item.getSp() - item.getRate())));
                        }
                        chunk20.setFont(descDarkGrayFont);
                        cell3.addElement(chunk20);
                        pdfPTable2.addCell(cell3);
                    }

                    position++;
                }

                document.add(pdfPTable2);
                document.add(lineSeparator);

                PdfPTable totalAmtPdfPTable = new PdfPTable(3);
                totalAmtPdfPTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totalAmtPdfPTable.setSpacingBefore(10);
                totalAmtPdfPTable.setSpacingAfter(10);
                totalAmtPdfPTable.setWidthPercentage(100);

                PdfPCell detailsBlankCell = new PdfPCell();
                detailsBlankCell.setBorder(Rectangle.NO_BORDER);
                detailsBlankCell.setVerticalAlignment(Element.ALIGN_TOP);
                detailsBlankCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                detailsBlankCell.addElement(new Chunk());
                totalAmtPdfPTable.addCell(detailsBlankCell);

                Chunk totAmtLabelChunk = new Chunk("Total Amount", descDarkGrayFont);
                PdfPCell totAmtLabelCell = new PdfPCell();
                Paragraph totAmtLabelParagraph = new Paragraph(totAmtLabelChunk);
                totAmtLabelParagraph.setAlignment(Element.ALIGN_RIGHT);
                totAmtLabelCell.setBorder(Rectangle.NO_BORDER);
                totAmtLabelCell.setUseAscender(true);
                totAmtLabelCell.setVerticalAlignment(Element.ALIGN_TOP);
                totAmtLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totAmtLabelCell.addElement(totAmtLabelParagraph);
                totalAmtPdfPTable.addCell(totAmtLabelCell);

                Chunk totAmtChunk = new Chunk(tvSubTotAmt.getText().toString(), descDarkGrayFont);
                PdfPCell totAmtCell = new PdfPCell();
                Paragraph totAmtParagraph = new Paragraph(totAmtChunk);
                totAmtParagraph.setAlignment(Element.ALIGN_RIGHT);
                totAmtCell.setBorder(Rectangle.NO_BORDER);
                totAmtCell.setUseAscender(true);
                totAmtCell.setVerticalAlignment(Element.ALIGN_TOP);
                totAmtCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totAmtCell.addElement(totAmtParagraph);
                totalAmtPdfPTable.addCell(totAmtCell);
                document.add(totalAmtPdfPTable);

                document.add(lineSeparator);

                PdfPTable amtDetailsPdfPTable = new PdfPTable(3);
                amtDetailsPdfPTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
                amtDetailsPdfPTable.setSpacingBefore(10);
                amtDetailsPdfPTable.setSpacingAfter(10);
                amtDetailsPdfPTable.setWidthPercentage(100);

                amtDetailsPdfPTable.addCell(detailsBlankCell);

                Chunk grossTotalLabelChunk = new Chunk("Gross Total", descFont);
                PdfPCell grossTotalLabelCell = new PdfPCell();
                Paragraph grossTotalLabelParagraph = new Paragraph(grossTotalLabelChunk);
                grossTotalLabelParagraph.setAlignment(Element.ALIGN_RIGHT);
                grossTotalLabelCell.setBorder(Rectangle.NO_BORDER);
                grossTotalLabelCell.setUseAscender(true);
                grossTotalLabelCell.setVerticalAlignment(Element.ALIGN_TOP);
                grossTotalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                grossTotalLabelCell.addElement(grossTotalLabelParagraph);
                amtDetailsPdfPTable.addCell(grossTotalLabelCell);

                Chunk grossTotalChunk = new Chunk(tvGrossTotAmt.getText().toString(), descFont);
                PdfPCell grossTotalCell = new PdfPCell();
                Paragraph grossTotalParagraph = new Paragraph(grossTotalChunk);
                grossTotalParagraph.setAlignment(Element.ALIGN_RIGHT);
                grossTotalCell.setBorder(Rectangle.NO_BORDER);
                grossTotalCell.setUseAscender(true);
                grossTotalCell.setVerticalAlignment(Element.ALIGN_TOP);
                grossTotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                grossTotalCell.addElement(grossTotalParagraph);
                amtDetailsPdfPTable.addCell(grossTotalCell);
                document.add(amtDetailsPdfPTable);

                document.add(lineSeparator);

                PdfPTable pdfPTable3 = new PdfPTable(4);
                pdfPTable3.setWidthPercentage(100);
                pdfPTable3.setSpacingAfter(10);
                String[] taxHeader = {"GST","Base Amt","Tax Amt",""};
                for(String header: taxHeader){
                    cell3 = new PdfPCell();
                    cell3.setPadding(cellPadding);
                    cell3.setVerticalAlignment(Element.ALIGN_TOP);
                    cell3.setBackgroundColor(baseLightBlue);
                    cell3.setBorder(Rectangle.TOP);
                    chunk20 = new Chunk(header,descFont);
                    cell3.addElement(chunk20);
                    pdfPTable3.addCell(cell3);
                }
                for(String header: taxHeader){
                    cell3 = new PdfPCell();
                    cell3.setPaddingTop(cellPadding);
                    cell3.setPaddingLeft(cellPadding);
                    cell3.setPaddingRight(cellPadding);
                    cell3.setVerticalAlignment(Element.ALIGN_TOP);
                    cell3.setBackgroundColor(baseLightBlue);
                    cell3.setBorder(Rectangle.TOP);
                    chunk20 = new Chunk();
                    if(header.equals("GST")){
                        chunk20.append("CGST");
                    }else if(header.equals("Base Amt")){
                        chunk20.append(textBaseCgstAmount.getText().toString());
                    }else if(header.equals("Tax Amt")){
                        chunk20.append(tvTotCgst.getText().toString());
                    }
                    chunk20.setFont(descGrayFont);
                    cell3.addElement(chunk20);
                    pdfPTable3.addCell(cell3);
                }
                for(String header: taxHeader){
                    cell3 = new PdfPCell();
                    cell3.setPaddingBottom(cellPadding);
                    cell3.setPaddingLeft(cellPadding);
                    cell3.setPaddingRight(cellPadding);
                    cell3.setVerticalAlignment(Element.ALIGN_TOP);
                    cell3.setBackgroundColor(baseLightBlue);
                    cell3.setBorder(Rectangle.NO_BORDER);
                    chunk20 = new Chunk();
                    if(header.equals("GST")){
                        chunk20.append("SGST");
                    }else if(header.equals("Base Amt")){
                        chunk20.append(textBaseSgstAmount.getText().toString());
                    }else if(header.equals("Tax Amt")){
                        chunk20.append(tvTotSgst.getText().toString());
                    }
                    chunk20.setFont(descGrayFont);
                    cell3.addElement(chunk20);
                    pdfPTable3.addCell(cell3);
                }
                document.add(pdfPTable3);
                document.add(lineSeparator);

                PdfPTable pdfPTable4 = new PdfPTable(1);
                pdfPTable4.setWidthPercentage(100);
                pdfPTable4.setSpacingBefore(spacing);

                cell3 = new PdfPCell();
                Paragraph paragraph1 = new Paragraph();
                chunk20 = new Chunk("CUSTOMER DETAILS", subHeaderOrangeFont);
                paragraph1.setSpacingAfter(5);
                paragraph1.add(chunk20);

                cell3.addElement(paragraph1);
                cell3.addElement(lineSeparator);
                cell3.setPadding(20);
                cell3.setUseAscender(true);
                cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell3.setVerticalAlignment(Element.ALIGN_TOP);
                cell3.setBorder(Rectangle.NO_BORDER);

                paragraph1 = new Paragraph();
                chunk20 = new Chunk(tvCustomerName.getText().toString(), descDarkGrayFont);
                paragraph1.add(chunk20);
                paragraph1.setSpacingBefore(15);
                cell3.addElement(paragraph1);

                paragraph1 = new Paragraph();
                chunk20 = new Chunk(textCustomerMobile.getText().toString(), descDarkGrayFont);
                paragraph1.add(chunk20);
                cell3.addElement(paragraph1);
                cell3.setBackgroundColor(baseLightBlue);
                pdfPTable4.addCell(cell3);
                document.add(pdfPTable4);

                PdfPTable pdfPTable5 = new PdfPTable(1);
                pdfPTable5.setWidthPercentage(100);
                pdfPTable5.setSpacingAfter(spacing);
                pdfPTable5.setSpacingBefore(spacing);

                cell3 = new PdfPCell();
                paragraph1 = new Paragraph();
                chunk20 = new Chunk("STORE DETAILS", subHeaderOrangeFont);
                paragraph1.setSpacingAfter(5);
                paragraph1.add(chunk20);

                cell3.addElement(paragraph1);
                cell3.addElement(lineSeparator);
                cell3.setPadding(20);
                cell3.setUseAscender(true);
                cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell3.setVerticalAlignment(Element.ALIGN_TOP);
                cell3.setBorder(Rectangle.NO_BORDER);

                paragraph1 = new Paragraph();
                chunk20 = new Chunk(tvShopName.getText().toString(), descDarkGrayFont);
                paragraph1.add(chunk20);
                paragraph1.setSpacingBefore(15);
                cell3.addElement(paragraph1);

                paragraph1 = new Paragraph();
                chunk20 = new Chunk(tvShopAddress.getText().toString(), descDarkGrayFont);
                paragraph1.add(chunk20);
                cell3.addElement(paragraph1);

                paragraph1 = new Paragraph();
                chunk20 = new Chunk(tvShopPhone.getText().toString(), descDarkGrayFont);
                paragraph1.add(chunk20);
                cell3.addElement(paragraph1);

                paragraph1 = new Paragraph();
                chunk20 = new Chunk(tvShopEmail.getText().toString(), descDarkGrayFont);
                paragraph1.add(chunk20);
                cell3.addElement(paragraph1);

                paragraph1 = new Paragraph();
                chunk20 = new Chunk(tvShopGSTIN.getText().toString(), descDarkGrayFont);
                paragraph1.add(chunk20);
                cell3.addElement(paragraph1);

                cell3.setBackgroundColor(baseLightOragnge);
                pdfPTable5.addCell(cell3);
                document.add(pdfPTable5);

                paragraph1 = new Paragraph();
                chunk20 = new Chunk("* Tax will be charged at applicable rate on the discounted products.",
                        descDarkGrayFont);
                paragraph1.add(chunk20);
                paragraph1.setSpacingAfter(30);

                document.add(paragraph1);

                paragraph1 = new Paragraph();
                chunk20 = new Chunk("Dear Customer",
                        descDarkGrayFont);
                paragraph1.add(chunk20);
                document.add(paragraph1);

                paragraph1 = new Paragraph();
                chunk20 = new Chunk("Thank you for shopping at Shoppurs.",
                        descDarkGrayFont);
                paragraph1.add(chunk20);
                document.add(paragraph1);

                paragraph1 = new Paragraph();
                chunk20 = new Chunk("In case you would like to Exchange any of the products purchased, we request you to carry" +
                        " your bill along. It will be our pleasure to serve you again.",
                        descDarkGrayFont);
                paragraph1.add(chunk20);
                paragraph1.setSpacingAfter(40);
                document.add(paragraph1);

                paragraph1 = new Paragraph();
                chunk20 = new Chunk(tvInvoiceNo.getText().toString(), descFont);
                paragraph1.add(chunk20);
                paragraph1.setSpacingAfter(30);
                paragraph1.setAlignment(Element.ALIGN_CENTER);
                document.add(paragraph1);

                paragraph1 = new Paragraph();
                chunk20 = new Chunk(invoiceNo, descBlueFont);
                paragraph1.add(chunk20);
                paragraph1.setAlignment(Element.ALIGN_CENTER);
                document.add(paragraph1);

                if(barcodeImage != null){
                    stream = new ByteArrayOutputStream();
                    barcodeImage.compress(Bitmap.CompressFormat.JPEG, 100 , stream);
                    myImg = Image.getInstance(stream.toByteArray());
                    myImg.setAlignment(Image.MIDDLE);
                    myImg.setSpacingAfter(30);
                    document.add(myImg);
                }

                //document.add(lineSeparator);
                document.close();

                if(actionType == SHARE){
                    share(fileName);
                }/*else if(actionType == PRINT){
                    print(fileName);
                }*/if(actionType == DOWNLOAD){
                    download(fileName);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void share(String path){
        File file = new File(path);
        Uri uri = FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".provider", file);

        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, uri);
       // share.setPackage("com.whatsapp");
        startActivity(share);
    }

    private void download(String path){
        DialogAndToast.showToast("Pdf downloaded successfully. The location is "+path,this);
    }

    private byte[] getFileByteArray(File file){
        FileInputStream fis = null;
        byte[] bytes = null;
        try {
            fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            try {
                for (int readNum; (readNum = fis.read(buf)) != -1;) {
                    bos.write(buf, 0, readNum); //no doubt here is 0
                    //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
                    System.out.println("read " + readNum + " bytes,");
                }
            } catch (IOException ex) {
                // Logger.getLogger(genJpeg.class.getName()).log(Level.SEVERE, null, ex);
            }
            bytes = bos.toByteArray();
            Log.i(TAG,"byte array is created");
            Log.i(TAG,"len "+bytes.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return bytes;
    }

    private String getfinalString(String string, String string2, int strlength) {
        int len1 = string.length();
        int len2 = string2.length();
        StringBuffer sb = new StringBuffer();
        sb.append(string);
        if (len1 + len2 < strlength) {
            int space = strlength - (len1 + len2);

            for(int i = 0; i < space; ++i) {
                sb.append(" ");
            }

            sb.append(string2);
        } else {
            sb.append(string2);
        }

        return sb.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private List<Bitmap> getBitmaps(File file){
        List<Bitmap> bitmaps = new ArrayList<>();
        try {
            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
            Bitmap bitmap;
            final int pageCount = renderer.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = renderer.openPage(i);

                int width = getResources().getDisplayMetrics().densityDpi / 72 * page.getWidth();
                int height = getResources().getDisplayMetrics().densityDpi / 72 * page.getHeight();
                width = 600;
                height = 1100;
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
                bitmap =  compressImage(bitmap);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                bitmaps.add(bitmap);
                //printer.print(0, bitmap, 30, TimeUnit.SECONDS);
                // close the page
                page.close();

            }
            // close the renderer
            renderer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmaps;
    }

    private Bitmap compressImage(Bitmap bitmap){
        int origWidth = bitmap.getWidth();
        int origHeight = bitmap.getHeight();
        Log.i(TAG,"width "+origWidth);
        Bitmap returnBitmap = null;
        int DEST_WIDTH = 300;
        if(origWidth > DEST_WIDTH){
            // picture is wider than we want it, we calculate its target height
            int destHeight = origHeight/( origWidth / DEST_WIDTH ) ;
            // we create an scaled bitmap so it reduces the image, not just trim it
            returnBitmap = Bitmap.createScaledBitmap(bitmap, DEST_WIDTH, destHeight, false);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            // compress to the format you want, JPEG, PNG...
            // 70 is the 0-100 quality percentage
            returnBitmap.compress(Bitmap.CompressFormat.JPEG,40 , outStream);
            Log.i(TAG,"image compressed");

        }else{
            returnBitmap = bitmap;
        }
        return returnBitmap;
    }

    private void generateBarcode(String text){
       /* if(text.length() < 12){
            int len = text.length();
            for(int i = len; i < 12; i++){
                text = "0"+text;
            }
        }*/
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.CODE_128,200,100);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            barcodeImage = barcodeEncoder.createBitmap(bitMatrix);
            image_barcode.setImageBitmap(barcodeImage);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

}
