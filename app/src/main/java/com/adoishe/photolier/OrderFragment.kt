package com.adoishe.photolier


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.math.BigInteger
import java.security.MessageDigest


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1          : String? = null
    private var param2          : String? = null
            var sendorder       : Boolean? = null
            var ordersHistory   : Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
    fun fillWebView(v: View){
        val webView = v.findViewById<WebView>(R.id.payWebView)
        // Enable the WebView to access content through file: URLs
        webView.settings.apply {
            allowFileAccess = true
            javaScriptEnabled = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
        }

// регистрационная информация (Идентификатор магазина, пароль #1)
// registration info (Merchant ID, password #1)
        val mrh_login = "MP.Photolier"
        val mrh_pass1 = "K4b53A0hjvZE1CtzDKwJ"

// номер заказа
        val inv_id = 12345

// описание заказа
// order description
        val inv_desc = "Техническая документация по ROBOKASSA"

// сумма заказа
// sum of order
        val out_summ = "8.96"

        // тип товара
// code of goods
        val shp_item = 1

// предлагаемая валюта платежа
// default payment e-currency
        val in_curr = "BANKOCEAN2R";

// язык
// language
        val culture = "ru"
// кодировка
// encoding
        val encoding = "utf-8"
// Адрес электронной почты покупателя
// E-mail
        val Email = "test@test.ru";
// Срок действия счёта
// Expiration Date
        val ExpirationDate = "2029-01-16T12:00";
// Валюта счёта
// OutSum Currency
        val OutSumCurrency = "USD"

        //------------------
        val isTest = 1
        //------------------
// формирование подписи
// generate signature
        //val crc  = md5("$mrh_login:$out_summ:$inv_id:$OutSumCurrency:$mrh_pass1:Shp_item=$shp_item");
        //val crc  = md5("$mrh_login:$out_summ:$inv_id:$inv_desc:$mrh_pass1:$shp_item:$isTest:Shp_item=$shp_item")
        val crc  =  md5("$mrh_login:$inv_id:$out_summ:$mrh_pass1")



        //val htmlCode = "<html><form action='https://auth.robokassa.ru/Merchant/Index.aspx' method=POST><input type=hidden name=MerchantLogin value=$mrh_login><input type=hidden name=OutSum value=$out_summ><input type=hidden name=InvId value=$inv_id><input type=hidden name=Description value='$inv_desc'><input type=hidden name=SignatureValue value=$crc><input type=hidden name=Shp_item value='$shp_item'><input type=hidden name=IncCurrLabel value=$in_curr><input type=hidden name=Culture value=$culture><input type=hidden name=Email value=$Email><input type=hidden name=ExpirationDate value=$ExpirationDate><input type=hidden name=OutSumCurrency value=$OutSumCurrency><input type=submit value='Оплатить'></form></html>"
        //val roboForm = "https://auth.robokassa.ru/Merchant/PaymentForm/FormMS.js"
       // val roboForm = "https://auth.robokassa.ru/Merchant/PaymentForm/FormL.js"
        val roboForm = "https://auth.robokassa.ru/Merchant/PaymentForm/FormSS.js"
       // val roboForm = "https://auth.robokassa.ru/Merchant/PaymentForm/FormFLS.js"

        val htmlCode = "<script type='text/javascript' src='https://auth.robokassa.ru/Merchant/PaymentForm/FormSS.js?MerchantLogin=MP.Photolier&InvoiceID=0&Culture=ru&Encoding=utf-8&OutSum=100&SignatureValue=54e25cb6e54f17e9cce01969771374cd'></script>"
        //"<html><script language=JavaScript src='$roboForm?MerchantLogin=$mrh_login&OutSum=$out_summ&InvoiceID=$inv_id&Description=$inv_desc&SignatureValue=$crc&Shp_item=$shp_item&IsTest=$isTest'></script></html>"
        //"<html><script language=JavaScript src='https://auth.robokassa.ru/Merchant/PaymentForm/FormMS.js?MerchantLogin=$mrh_login&OutSum=$out_summ&InvoiceID=$inv_id&Description=$inv_desc&SignatureValue=$crc'></script></html>"

// ГЕНЕРАЦИЯ САЙТА
        //"<script type='text/javascript' src='https://auth.robokassa.ru/Merchant/PaymentForm/FormSS.js?MerchantLogin=MP.Photolier&InvoiceID=0&Culture=ru&Encoding=utf-8&OutSum=100&SignatureValue=54e25cb6e54f17e9cce01969771374cd'></script>"

// КАНДИДАТ ЕТСТ
 //       "<html><script language=JavaScript src='https://auth.robokassa.ru/Merchant/PaymentForm/FormSS.js?MerchantLogin=$mrh_login&InvoiceID=$inv_id&Culture=ru&Encoding=utf-8&Description=$inv_desc&OutSum=$out_summ&SignatureValue=$crc&IsTest=$isTest"


       // val htmlText = "<html><body>$htmlCode</body></html>"

        webView.loadDataWithBaseURL(null, htmlCode, "text/html", "ru_RU", null);
    }

    private fun fillBySend(v: View){

        val textViewResult      = v.findViewById<TextView>(R.id.textViewResult)
        val orderUuid           = arguments?.getString("orderUuid")
        val orderName           = arguments?.getString("orderName")
        val orderStatus         = arguments?.getString("orderStatus")
            textViewResult.text = orderUuid + "\n" + orderName+ "\n" + orderStatus

        fillWebView(v)

    }
    private fun fillByHistory(v: View){

        val textViewResult      = v.findViewById<TextView>(R.id.textViewResult)
        val orderText           = arguments?.getString("orderText")
            textViewResult.text = orderText

        fillWebView(v)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val root            = inflater.inflate(R.layout.fragment_order, container, false)
            sendorder       = arguments?.getBoolean("sendorder")
            ordersHistory   = arguments?.getBoolean("ordersHistory")

        when {
            sendorder!! -> {

                fillBySend(root)

                val main        = (requireActivity() as MainActivity)
                    main.order  = Order(main)
            }
            ordersHistory!! -> {

                fillByHistory(root)

            }

        }
        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}