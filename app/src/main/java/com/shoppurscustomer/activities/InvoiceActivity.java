package com.shoppurscustomer.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.shoppurscustomer.R;
import com.shoppurscustomer.adapters.InvoiceItemAdapter;
import com.shoppurscustomer.models.Coupon;
import com.shoppurscustomer.models.InvoiceItem;
import com.shoppurscustomer.utilities.ConnectionDetector;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.DialogAndToast;
import com.shoppurscustomer.utilities.EnglishNumberToWords;
import com.shoppurscustomer.utilities.Utility;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceActivity extends NetworkBaseActivity {

    private final int SHARE =1,PRINT =2,DOWNLOAD=3;

    private RecyclerView recyclerView;
    private List<InvoiceItem> itemList;
    private InvoiceItemAdapter itemAdapter;

    private TextView tvShopName,tvShopAddress,tvShopEmail,tvShopPhone,tvShopGSTIN,tvInvoiceNo,tvDate,tvCustomerName,
                      tvSubTotAmt,tvGrossTotAmt,tvTotSgst,tvTotCgst,tvTotIgst,tvShortExcess,tvNetPayableAmt,tvNetPayableWords,
                      tvPaidAmt,tvBalAmt,tvTotQty,tvDiscount,tvPaymentMethod,tvPaymentBrand,tvTransId,tvPaymentAmount,tvTotSavings;

    private RelativeLayout rlCouponLayout;
    private TextView tvCouponOfferName;
    private int actionType;

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
        tvSubTotAmt = findViewById(R.id.text_sub_total_amount);
        tvGrossTotAmt = findViewById(R.id.text_gross_total_amount);
        tvTotSgst = findViewById(R.id.text_sgst);
        tvTotCgst = findViewById(R.id.text_cgst);
        tvTotIgst = findViewById(R.id.text_igst);
        tvShortExcess = findViewById(R.id.text_short_excess);
        tvNetPayableAmt = findViewById(R.id.text_net_payable_amount);
        tvNetPayableWords = findViewById(R.id.text_net_payable_amount_words);
        tvPaidAmt = findViewById(R.id.text_paid_amount);
        tvBalAmt = findViewById(R.id.text_balance);
        tvDiscount = findViewById(R.id.text_discount);
        tvPaymentMethod = findViewById(R.id.text_payment_method);
        tvPaymentBrand = findViewById(R.id.text_payment_brand);
        tvTransId = findViewById(R.id.text_payment_transaction_id);
        tvPaymentAmount = findViewById(R.id.text_paid_amount);
        tvTotSavings = findViewById(R.id.text_total_savings);
        tvTotQty = findViewById(R.id.text_total_qty);

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
                createPdf();
            }
        });

        ivPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionType = PRINT;
                createPdf();
            }
        });

        ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionType = DOWNLOAD;
                createPdf();
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
        String url=getResources().getString(R.string.root_url)+Constants.GET_INVOICE;
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
                    tvInvoiceNo.setText("Invoice No: "+jsonObject.getString("invNo"));
                    float subTotal = Float.parseFloat(""+(jsonObject.getDouble("invTotAmount") - jsonObject.getDouble("invTotTaxAmount")));
                    tvSubTotAmt.setText(Utility.numberFormat(subTotal));
                    tvGrossTotAmt.setText(Utility.numberFormat(subTotal));
                    tvTotIgst.setText(Utility.numberFormat(jsonObject.getDouble("invTotIGST")));
                    tvTotSgst.setText(Utility.numberFormat(jsonObject.getDouble("invTotSGST")));
                    tvTotCgst.setText(Utility.numberFormat(jsonObject.getDouble("invTotCGST")));
                   // tvShortExcess.setText(Utility.numberFormat(jsonObject.getDouble("invTotDisAmount")));
                    float netPayable = (float) Math.round(jsonObject.getDouble("invTotNetPayable"));
                    tvNetPayableAmt.setText(Utility.numberFormat(netPayable));
                    tvShortExcess.setText(Utility.numberFormat(netPayable - (float)jsonObject.getDouble("invTotNetPayable")));
                    tvPaidAmt.setText(Utility.numberFormat(netPayable));
                    tvNetPayableWords.setText(EnglishNumberToWords.convert((int)netPayable)+" rupees");
                    tvPaymentMethod.setText(jsonObject.getString("paymentMethod"));
                    tvPaymentBrand.setText(jsonObject.getString("paymentBrand"));
                    tvTransId.setText(jsonObject.getString("invTransId"));
                    tvPaymentAmount.setText(Utility.numberFormat(netPayable));
                    tvTotSavings.setText("Total Savings(Rupees) "+Utility.numberFormat(jsonObject.getDouble("invTotDisAmount")));
                    tvDiscount.setText(Utility.numberFormat(jsonObject.getDouble("invTotDisAmount")));

                    int couponId = jsonObject.getInt("invCoupenId");
                    if(couponId == 0){
                        rlCouponLayout.setVisibility(View.GONE);
                    }else{
                        rlCouponLayout.setVisibility(View.VISIBLE);
                        Coupon coupon = dbHelper.getCouponOffer(String.valueOf(couponId));
                        tvCouponOfferName.setText(coupon.getName());
                    }

                    int len = invoiceDetailsArray.length();
                  //  InvoiceDetail invoiceDetail= null;
                    int totQty = 0;
                    for (int i = 0; i < len; i++) {
                        jsonObject = invoiceDetailsArray.getJSONObject(i);
                        //invoiceDetail = new InvoiceDetail();

                        InvoiceItem item = new InvoiceItem();
                        item.setItemName(jsonObject.getString("invDProdName"));
                        item.setHsn(jsonObject.getString("invDHsnCode"));
                        item.setQty(jsonObject.getInt("invDQty"));
                        item.setGst(jsonObject.getInt("invDIGST"));
                        item.setMrp((float) jsonObject.getDouble("invDMrp"));
                        item.setRate(Float.parseFloat(jsonObject.getString("invDSp")));
                        item.setFreeItems(jsonObject.getInt("invDFreeItems"));
                        item.setOfferId(jsonObject.getString("invdOfferId"));
                        item.setOfferType(jsonObject.getString("invdOfferType"));
                        item.setUnit(jsonObject.getString("invdProdUnit"));
                        item.setColor(jsonObject.getString("invdProdColor"));
                        item.setSize(jsonObject.getString("invdProdSize"));
                        itemList.add(item);
                        totQty = totQty + item.getQty();
                    }

                    tvTotQty.setText(""+totQty);
                    itemAdapter.notifyDataSetChanged();

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
        String date=new SimpleDateFormat("yyyy_MM_dd").format(new Date());
        String time=new SimpleDateFormat("HH:mm:ss").format(new Date());

        String root="";
        if(BaseImageActivity.isExternalStorageAvailable()){
            root = Environment.getExternalStoragePublicDirectory("").toString();
        }
        File myDir = new File(root+"/Shoppurs/Shoppurs documents");
        myDir.mkdirs();
        String fname="";
        fname = date+time+".pdf";

        String imagePath=root+"/Shoppurs/Shoppurs documents/"+fname;
       // File file = new File (myDir, fname);
        return imagePath;

    }


    private void createPdf(){

        String fileName = getFile();

        /***
         * Variables for further use....
         */
        BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
        float mHeadingFontSize = 20.0f;
        float mValueFontSize = 26.0f;

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
            document.addCreator(sharedPreferences.getString(Constants.FULL_NAME,""));


            // Title Order Details...
// Adding Title....
            Font headerFont = new Font(baseFont, 20.0f, Font.NORMAL, BaseColor.BLACK);
            Font descFont = new Font(baseFont, 15.0f, Font.NORMAL, BaseColor.BLACK);

            String shopName = sharedPreferences.getString(Constants.SHOP_INSIDE_NAME,"");
            shopName = shopName.toUpperCase();

// Creating Chunk
            Chunk shopNameChunk = new Chunk(shopName, headerFont);
// Creating Paragraph to add...
            Paragraph shopNameParagraph = new Paragraph(shopNameChunk);
// Setting Alignment for Heading
            shopNameParagraph.setAlignment(Element.ALIGN_CENTER);
// Finally Adding that Chunk
            document.add(shopNameParagraph);

            document.add(new Paragraph(""));
            document.add(new Paragraph(""));

            String shopAddress = sharedPreferences.getString(Constants.ADDRESS,"");
            Chunk shopAddressChunk = new Chunk(shopAddress, descFont);
            Paragraph shopAddressParagraph = new Paragraph(shopAddressChunk);
            shopAddressParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(shopAddressParagraph);

            String shopPhone = "Ph: "+sharedPreferences.getString(Constants.MOBILE_NO,"");
            Chunk shopPhoneChunk = new Chunk(shopPhone, descFont);
            Paragraph shopMobileParagraph = new Paragraph(shopPhoneChunk);
            shopMobileParagraph.setSpacingBefore(20);
            shopMobileParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(shopMobileParagraph);

            String shopEmail = "Email: "+sharedPreferences.getString(Constants.EMAIL,"");
            Chunk shopEmailChunk = new Chunk(shopEmail, descFont);
            Paragraph shopEmailParagraph = new Paragraph(shopEmailChunk);
            shopEmailParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(shopEmailParagraph);

            String shopGSTIN = "GSTIN: "+sharedPreferences.getString(Constants.GST_NO,"");
            Chunk shopGSTINChunk = new Chunk(shopGSTIN, descFont);
            Paragraph shopGSTINParagraph = new Paragraph(shopGSTINChunk);
            shopGSTINParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(shopGSTINParagraph);

            Chunk taxInvoiceChunk = new Chunk("Tax Invoice", descFont);
            Paragraph taxInvoiceParagraph = new Paragraph(taxInvoiceChunk);
            taxInvoiceParagraph.setSpacingBefore(10);
           // taxInvoiceParagraph.setSpacingAfter(10);
            taxInvoiceParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(taxInvoiceParagraph);


            document.add(new Chunk(lineSeparator));

            Chunk invoiceNoChunk = new Chunk(tvInvoiceNo.getText().toString(), headerFont);
            Paragraph invoiceNoParagraph = new Paragraph(invoiceNoChunk);
            invoiceNoParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(invoiceNoParagraph);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Chunk dateChunk = new Chunk(tvDate.getText().toString(), headerFont);
            Chunk nameChunk = new Chunk(tvCustomerName.getText().toString(), headerFont);
            Paragraph dateParagraph = new Paragraph(dateChunk);
            dateParagraph.add(new Chunk(glue));
            dateParagraph.add(nameChunk);
            document.add(dateParagraph);

            document.add(new Chunk(lineSeparator));

            PdfPTable pdfPTable = new PdfPTable(6);
            pdfPTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pdfPTable.setWidthPercentage(100);

            Chunk itemNameLabelChunk = new Chunk("Item Name", descFont);
            PdfPCell itemNameLabelCell = new PdfPCell();
            itemNameLabelCell.setUseAscender(true);
            itemNameLabelCell.setVerticalAlignment(Element.ALIGN_TOP);
            itemNameLabelCell.setBorder(Rectangle.NO_BORDER);
            itemNameLabelCell.addElement(itemNameLabelChunk);
           // itemNameCell.setColspan(2);
            pdfPTable.addCell(itemNameLabelCell);

           // Paragraph itemMenuParagraph = new Paragraph(itemNameChunk);
            Chunk hsnCodeLabelChunk = new Chunk("HSN", descFont);
            PdfPCell hsnCodeLabelCell = new PdfPCell();
            hsnCodeLabelCell.setUseAscender(true);
            hsnCodeLabelCell.setVerticalAlignment(Element.ALIGN_TOP);
            Paragraph hsnParagraphLabel = new Paragraph(hsnCodeLabelChunk);
            hsnParagraphLabel.setAlignment(Element.ALIGN_RIGHT);
            hsnCodeLabelCell.setBorder(Rectangle.NO_BORDER);
            hsnCodeLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            hsnCodeLabelCell.addElement(hsnParagraphLabel);
            pdfPTable.addCell(hsnCodeLabelCell);


            Chunk qtyCodeLabelChunk = new Chunk("QTY", descFont);
            PdfPCell qtyLabelCell = new PdfPCell();
            qtyLabelCell.setUseAscender(true);
            qtyLabelCell.setVerticalAlignment(Element.ALIGN_TOP);
            Paragraph qtyLabelParagraph = new Paragraph(qtyCodeLabelChunk);
            qtyLabelParagraph.setAlignment(Element.ALIGN_RIGHT);
            qtyLabelCell.setBorder(Rectangle.NO_BORDER);
            qtyLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            qtyLabelCell.addElement(qtyLabelParagraph);
            pdfPTable.addCell(qtyLabelCell);

            Chunk mrpLabelChunk = new Chunk("MRP", descFont);
            PdfPCell mrpLabelCell = new PdfPCell();
            Paragraph mrpLabelParagraph = new Paragraph(mrpLabelChunk);
            mrpLabelParagraph.setAlignment(Element.ALIGN_RIGHT);
            mrpLabelCell.setBorder(Rectangle.NO_BORDER);
            mrpLabelCell.setUseAscender(true);
            mrpLabelCell.setVerticalAlignment(Element.ALIGN_TOP);
            mrpLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            mrpLabelCell.addElement(mrpLabelParagraph);
            pdfPTable.addCell(mrpLabelCell);

            Chunk rateLabelChunk = new Chunk("RATE", descFont);
            PdfPCell rateLabelCell = new PdfPCell();
            Paragraph rateLabelParagraph = new Paragraph(rateLabelChunk);
            rateLabelParagraph.setAlignment(Element.ALIGN_RIGHT);
            rateLabelCell.setBorder(Rectangle.NO_BORDER);
            rateLabelCell.setUseAscender(true);
            rateLabelCell.setVerticalAlignment(Element.ALIGN_TOP);
            rateLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            rateLabelCell.addElement(rateLabelParagraph);
            pdfPTable.addCell(rateLabelCell);

            Chunk amtLabelChunk = new Chunk("AMT", descFont);
            PdfPCell amtLabelCell = new PdfPCell();
            Paragraph amtLabelParagraph = new Paragraph(amtLabelChunk);
            amtLabelParagraph.setAlignment(Element.ALIGN_RIGHT);
            amtLabelCell.setBorder(Rectangle.NO_BORDER);
            amtLabelCell.setUseAscender(true);
            amtLabelCell.setVerticalAlignment(Element.ALIGN_TOP);
            amtLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            amtLabelCell.addElement(amtLabelParagraph);
            pdfPTable.addCell(amtLabelCell);


            document.add(pdfPTable);

            document.add(new Chunk(lineSeparator));

            PdfPTable itemPdfPTable = new PdfPTable(6);
            itemPdfPTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemPdfPTable.setWidthPercentage(100);

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
                Chunk itemNameChunk = new Chunk(name, descFont);
                PdfPCell itemNameCell = new PdfPCell();
                itemNameCell.setUseAscender(true);
                itemNameCell.setVerticalAlignment(Element.ALIGN_TOP);
                itemNameCell.setBorder(Rectangle.NO_BORDER);
                itemNameCell.addElement(itemNameChunk);
                itemNameCell.setColspan(6);
                itemPdfPTable.addCell(itemNameCell);

                PdfPCell blankCell = new PdfPCell();
                blankCell.setUseAscender(true);
                blankCell.setVerticalAlignment(Element.ALIGN_TOP);
                blankCell.setBorder(Rectangle.NO_BORDER);
                blankCell.addElement(new Chunk());
                itemPdfPTable.addCell(blankCell);

                Chunk hsnCodeChunk = new Chunk(item.getHsn(), descFont);
                PdfPCell hsnCodeCell = new PdfPCell();
                hsnCodeCell.setUseAscender(true);
                hsnCodeCell.setVerticalAlignment(Element.ALIGN_TOP);
                Paragraph hsnParagraph = new Paragraph(hsnCodeChunk);
                hsnParagraph.setAlignment(Element.ALIGN_RIGHT);
                hsnCodeCell.setBorder(Rectangle.NO_BORDER);
                hsnCodeCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                hsnCodeCell.addElement(hsnParagraph);
                itemPdfPTable.addCell(hsnCodeCell);

                String qty = "";
                if(item.getUnit() == null || item.getUnit().toLowerCase().equals("null") || item.getUnit().equals("")){
                    qty = ""+item.getQty();
                }else{
                    String[] unitArray = item.getUnit().split(" ");
                    float totalUnit = Float.parseFloat(unitArray[0]) * item.getQty();
                    qty = String.format("%.00f",totalUnit)+" "+unitArray[1];
                }

                Chunk qtyCodeChunk = new Chunk(qty, descFont);
                PdfPCell qtyCell = new PdfPCell();
                qtyCell.setUseAscender(true);
                qtyCell.setVerticalAlignment(Element.ALIGN_TOP);
                Paragraph qtyParagraph = new Paragraph(qtyCodeChunk);
                qtyParagraph.setAlignment(Element.ALIGN_RIGHT);
                qtyCell.setBorder(Rectangle.NO_BORDER);
                qtyCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                qtyCell.addElement(qtyParagraph);
                itemPdfPTable.addCell(qtyCell);

                Chunk mrpChunk = new Chunk(Utility.numberFormat(item.getMrp()), descFont);
                PdfPCell mrpCell = new PdfPCell();
                Paragraph mrpParagraph = new Paragraph(mrpChunk);
                mrpParagraph.setAlignment(Element.ALIGN_RIGHT);
                mrpCell.setBorder(Rectangle.NO_BORDER);
                mrpCell.setUseAscender(true);
                mrpCell.setVerticalAlignment(Element.ALIGN_TOP);
                mrpCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                mrpCell.addElement(mrpParagraph);
                itemPdfPTable.addCell(mrpCell);

                Chunk rateChunk = new Chunk(Utility.numberFormat(item.getRate()), descFont);
                PdfPCell rateCell = new PdfPCell();
                Paragraph rateParagraph = new Paragraph(rateChunk);
                rateParagraph.setAlignment(Element.ALIGN_RIGHT);
                rateCell.setBorder(Rectangle.NO_BORDER);
                rateCell.setUseAscender(true);
                rateCell.setVerticalAlignment(Element.ALIGN_TOP);
                rateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                rateCell.addElement(rateParagraph);
                itemPdfPTable.addCell(rateCell);

                float amt = item.getQty() * item.getRate();
                Chunk amtChunk = new Chunk(Utility.numberFormat(amt), descFont);
                PdfPCell amtCell = new PdfPCell();
                Paragraph amtParagraph = new Paragraph(amtChunk);
                amtParagraph.setAlignment(Element.ALIGN_RIGHT);
                amtCell.setBorder(Rectangle.NO_BORDER);
                amtCell.setUseAscender(true);
                amtCell.setVerticalAlignment(Element.ALIGN_TOP);
                amtCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                amtCell.addElement(amtParagraph);
                itemPdfPTable.addCell(amtCell);
            }

            document.add(itemPdfPTable);

            document.add(new Chunk(lineSeparator));

            PdfPTable totalAmtPdfPTable = new PdfPTable(3);
            totalAmtPdfPTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalAmtPdfPTable.setWidthPercentage(100);

            Chunk totQtyChunk = new Chunk("Total Qty     "+tvTotQty.getText().toString(), descFont);
            PdfPCell totQtyCell = new PdfPCell();
            Paragraph totQtyParagraph = new Paragraph(totQtyChunk);
            totQtyParagraph.setAlignment(Element.ALIGN_LEFT);
            totQtyCell.setBorder(Rectangle.NO_BORDER);
            totQtyCell.setUseAscender(true);
            totQtyCell.setVerticalAlignment(Element.ALIGN_TOP);
            totQtyCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            totQtyCell.addElement(totQtyParagraph);
            totalAmtPdfPTable.addCell(totQtyCell);

            Chunk totAmtLabelChunk = new Chunk("Total Amount", descFont);
            PdfPCell totAmtLabelCell = new PdfPCell();
            Paragraph totAmtLabelParagraph = new Paragraph(totAmtLabelChunk);
            totAmtLabelParagraph.setAlignment(Element.ALIGN_RIGHT);
            totAmtLabelCell.setBorder(Rectangle.NO_BORDER);
            totAmtLabelCell.setUseAscender(true);
            totAmtLabelCell.setVerticalAlignment(Element.ALIGN_TOP);
            totAmtLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totAmtLabelCell.addElement(totAmtLabelParagraph);
            totalAmtPdfPTable.addCell(totAmtLabelCell);

            Chunk totAmtChunk = new Chunk(tvSubTotAmt.getText().toString(), descFont);
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

            document.add(new Chunk(lineSeparator));

            PdfPTable amtDetailsPdfPTable = new PdfPTable(3);
            amtDetailsPdfPTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            amtDetailsPdfPTable.setWidthPercentage(100);

            PdfPCell detailsBlankCell = new PdfPCell();
            detailsBlankCell.setBorder(Rectangle.NO_BORDER);
            detailsBlankCell.setVerticalAlignment(Element.ALIGN_TOP);
            detailsBlankCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            detailsBlankCell.addElement(new Chunk());
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


            PdfPCell detailsBlankCell2 = new PdfPCell();
            detailsBlankCell2.setBorder(Rectangle.NO_BORDER);
            detailsBlankCell2.setVerticalAlignment(Element.ALIGN_TOP);
            detailsBlankCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            detailsBlankCell2.addElement(new Chunk());
            amtDetailsPdfPTable.addCell(detailsBlankCell2);

            Chunk sgstLabelChunk = new Chunk("+SGST", descFont);
            PdfPCell sgstLabelCell = new PdfPCell();
            Paragraph sgstLabelParagraph = new Paragraph(sgstLabelChunk);
            sgstLabelParagraph.setAlignment(Element.ALIGN_RIGHT);
            sgstLabelCell.setBorder(Rectangle.NO_BORDER);
            sgstLabelCell.setUseAscender(true);
            sgstLabelCell.setVerticalAlignment(Element.ALIGN_TOP);
            sgstLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sgstLabelCell.addElement(sgstLabelParagraph);
            amtDetailsPdfPTable.addCell(sgstLabelCell);

            Chunk sgstChunk = new Chunk(tvTotSgst.getText().toString(), descFont);
            PdfPCell sgstCell = new PdfPCell();
            Paragraph sgstParagraph = new Paragraph(sgstChunk);
            sgstParagraph.setAlignment(Element.ALIGN_RIGHT);
            sgstCell.setBorder(Rectangle.NO_BORDER);
            sgstCell.setUseAscender(true);
            sgstCell.setVerticalAlignment(Element.ALIGN_TOP);
            sgstCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sgstCell.addElement(sgstParagraph);
            amtDetailsPdfPTable.addCell(sgstCell);

            PdfPCell detailsBlankCell3 = new PdfPCell();
            detailsBlankCell3.setBorder(Rectangle.NO_BORDER);
            detailsBlankCell3.setVerticalAlignment(Element.ALIGN_TOP);
            detailsBlankCell3.setHorizontalAlignment(Element.ALIGN_LEFT);
            detailsBlankCell3.addElement(new Chunk());
            amtDetailsPdfPTable.addCell(detailsBlankCell3);

            Chunk cgstLabelChunk = new Chunk("+CGST", descFont);
            PdfPCell cgstLabelCell = new PdfPCell();
            Paragraph cgstLabelParagraph = new Paragraph(cgstLabelChunk);
            cgstLabelParagraph.setAlignment(Element.ALIGN_RIGHT);
            cgstLabelCell.setBorder(Rectangle.NO_BORDER);
            cgstLabelCell.setUseAscender(true);
            cgstLabelCell.setVerticalAlignment(Element.ALIGN_TOP);
            cgstLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cgstLabelCell.addElement(cgstLabelParagraph);
            amtDetailsPdfPTable.addCell(cgstLabelCell);

            Chunk cgstChunk = new Chunk(tvTotCgst.getText().toString(), descFont);
            PdfPCell cgstCell = new PdfPCell();
            Paragraph cgstParagraph = new Paragraph(cgstChunk);
            cgstParagraph.setAlignment(Element.ALIGN_RIGHT);
            cgstCell.setBorder(Rectangle.NO_BORDER);
            cgstCell.setUseAscender(true);
            cgstCell.setVerticalAlignment(Element.ALIGN_TOP);
            cgstCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cgstCell.addElement(cgstParagraph);
            amtDetailsPdfPTable.addCell(cgstCell);

            PdfPCell detailsBlankCell4 = new PdfPCell();
            detailsBlankCell4.setBorder(Rectangle.NO_BORDER);
            detailsBlankCell4.setVerticalAlignment(Element.ALIGN_TOP);
            detailsBlankCell4.setHorizontalAlignment(Element.ALIGN_LEFT);
            detailsBlankCell4.addElement(new Chunk());
            amtDetailsPdfPTable.addCell(detailsBlankCell4);

            Chunk shortExcessLabelChunk = new Chunk("Short/Excess(+/-)", descFont);
            PdfPCell shortExcessLabelCell = new PdfPCell();
            Paragraph shortExcessLabelParagraph = new Paragraph(shortExcessLabelChunk);
            shortExcessLabelParagraph.setAlignment(Element.ALIGN_RIGHT);
            shortExcessLabelCell.setBorder(Rectangle.NO_BORDER);
            shortExcessLabelCell.setUseAscender(true);
            shortExcessLabelCell.setVerticalAlignment(Element.ALIGN_TOP);
            shortExcessLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            shortExcessLabelCell.addElement(shortExcessLabelParagraph);
            amtDetailsPdfPTable.addCell(shortExcessLabelCell);

            Chunk shortExcessChunk = new Chunk(tvShortExcess.getText().toString(), descFont);
            PdfPCell shortExcessCell = new PdfPCell();
            Paragraph shortExcessParagraph = new Paragraph(shortExcessChunk);
            shortExcessParagraph.setAlignment(Element.ALIGN_RIGHT);
            shortExcessCell.setBorder(Rectangle.NO_BORDER);
            shortExcessCell.setUseAscender(true);
            shortExcessCell.setVerticalAlignment(Element.ALIGN_TOP);
            shortExcessCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            shortExcessCell.addElement(shortExcessParagraph);
            amtDetailsPdfPTable.addCell(shortExcessCell);

            document.add(amtDetailsPdfPTable);

            document.add(new Chunk(lineSeparator));

            PdfPTable netPayablePdfPTable = new PdfPTable(3);
            netPayablePdfPTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            netPayablePdfPTable.setWidthPercentage(100);

            PdfPCell netPayableBlankCell = new PdfPCell();
            netPayableBlankCell.setBorder(Rectangle.NO_BORDER);
            netPayableBlankCell.setVerticalAlignment(Element.ALIGN_TOP);
            netPayableBlankCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            netPayableBlankCell.addElement(new Chunk());
            netPayablePdfPTable.addCell(netPayableBlankCell);

            Chunk netPayableLabelChunk = new Chunk("Net Payable", descFont);
            PdfPCell netPayableLabelCell = new PdfPCell();
            Paragraph netPayableLabelParagraph = new Paragraph(netPayableLabelChunk);
            netPayableLabelParagraph.setAlignment(Element.ALIGN_RIGHT);
            netPayableLabelCell.setBorder(Rectangle.NO_BORDER);
            netPayableLabelCell.setUseAscender(true);
            netPayableLabelCell.setVerticalAlignment(Element.ALIGN_TOP);
            netPayableLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            netPayableLabelCell.addElement(netPayableLabelParagraph);
            netPayablePdfPTable.addCell(netPayableLabelCell);

            Chunk netPayablelChunk = new Chunk(tvNetPayableAmt.getText().toString(), descFont);
            PdfPCell netPayableCell = new PdfPCell();
            Paragraph netPayableParagraph = new Paragraph(netPayablelChunk);
            netPayableParagraph.setAlignment(Element.ALIGN_RIGHT);
            netPayableCell.setBorder(Rectangle.NO_BORDER);
            netPayableCell.setUseAscender(true);
            netPayableCell.setVerticalAlignment(Element.ALIGN_TOP);
            netPayableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            netPayableCell.addElement(netPayableParagraph);
            netPayablePdfPTable.addCell(netPayableCell);

            document.add(netPayablePdfPTable);

            document.add(new Chunk(lineSeparator));

            Chunk glueDis = new Chunk(new VerticalPositionMark());
            Chunk totDisLabelChunk = new Chunk("Total Discount", descFont);
            Chunk totDisChunk = new Chunk(tvDiscount.getText().toString(), descFont);
            Paragraph totDisParagraph = new Paragraph(totDisLabelChunk);
            totDisParagraph.add(new Chunk(glueDis));
            totDisParagraph.add(totDisChunk);
            document.add(totDisParagraph);

            document.add(new Chunk(lineSeparator));

            Chunk netWordsChunk = new Chunk(tvNetPayableWords.getText().toString(), descFont);
            Paragraph netWordsParagraph = new Paragraph(netWordsChunk);
            document.add(netWordsParagraph);

            document.add(new Chunk(lineSeparator));

            if(rlCouponLayout.getVisibility() == View.VISIBLE){
                Chunk couponNameChunk = new Chunk(tvCouponOfferName.getText().toString(), descFont);
                Paragraph couponNameParagraph = new Paragraph(couponNameChunk);
                document.add(couponNameParagraph);

                Chunk couponDescChunk = new Chunk("Offer applied on this bill", descFont);
                Paragraph couponDescParagraph = new Paragraph(couponDescChunk);
                document.add(couponDescParagraph);
            }

            Chunk glue2 = new Chunk(new VerticalPositionMark());
            Chunk paymentMethodLabelChunk = new Chunk("Payment Method", descFont);
            Chunk paymentMethodChunk = new Chunk(tvPaymentMethod.getText().toString(), descFont);
            Paragraph paymentMethodParagraph = new Paragraph(paymentMethodLabelChunk);
            paymentMethodParagraph.add(new Chunk(glue2));
            paymentMethodParagraph.add(paymentMethodChunk);
            document.add(paymentMethodParagraph);

            //document.add(new Chunk(lineSeparator));

            Chunk glue3 = new Chunk(new VerticalPositionMark());
            Chunk brandLabelChunk = new Chunk("Brand", descFont);
            Chunk brandChunk = new Chunk(tvPaymentBrand.getText().toString(), descFont);
            Paragraph brandParagraph = new Paragraph(brandLabelChunk);
            brandParagraph.add(new Chunk(glue3));
            brandParagraph.add(brandChunk);
            document.add(brandParagraph);

           // document.add(new Chunk(lineSeparator));

            Chunk glue4 = new Chunk(new VerticalPositionMark());
            Chunk transIdLabelChunk = new Chunk("Transaction Id", descFont);
            Chunk transIdChunk = new Chunk(tvTransId.getText().toString(), descFont);
            Paragraph transIdParagraph = new Paragraph(transIdLabelChunk);
            transIdParagraph.add(new Chunk(glue4));
            transIdParagraph.add(transIdChunk);
            document.add(transIdParagraph);

            document.add(new Chunk(lineSeparator));


            Chunk totSavingChunk = new Chunk(tvTotSavings.getText().toString(), descFont);
            Paragraph totSavingParagraph = new Paragraph(totSavingChunk);
            totSavingParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(totSavingParagraph);

            Chunk haveNiceDayChunk = new Chunk("HAVE A NICE DAY", descFont);
            Paragraph haveNiceDayParagraph = new Paragraph(haveNiceDayChunk);
            haveNiceDayParagraph.setAlignment(Element.ALIGN_CENTER);
            haveNiceDayParagraph.setSpacingAfter(20);
            haveNiceDayParagraph.setSpacingBefore(20);
            document.add(haveNiceDayParagraph);

            Chunk belongingsChunk = new Chunk("Please take care of your belongings", descFont);
            Paragraph belongingsParagraph = new Paragraph(belongingsChunk);
            belongingsParagraph.setAlignment(Element.ALIGN_CENTER);
          //  belongingsParagraph.setSpacingAfter(20);
          //  belongingsParagraph.setSpacingBefore(20);
            document.add(belongingsParagraph);

            document.add(new Chunk(lineSeparator));

            document.close();

            if(actionType == SHARE){
                share(fileName);
            }else if(actionType == PRINT){
                print(fileName);
            }if(actionType == DOWNLOAD){
                download(fileName);
            }

        } catch (DocumentException e) {
            e.printStackTrace();
            DialogAndToast.showToast("Error in creating pdf.",this);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            DialogAndToast.showToast("Error in creating pdf.",this);
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

    private void print(String path){
        DialogAndToast.showToast("Printing functionality is not working.",this);
    }

    private void download(String path){
        DialogAndToast.showToast("Pdf downloaded successfully. The location is "+path,this);
    }

}
