package com.jin.businfo_gumi.model.repository

import com.jin.businfo_gumi.model.data.DataLiveInfoForRoute
import okhttp3.ResponseBody
import org.xml.sax.InputSource
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import javax.xml.parsers.SAXParserFactory

class XmlConverterFactoryForRoute : Converter.Factory() {
    companion object {
        fun create() = XmlConverterFactoryForRoute()
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ) = Converter<ResponseBody, DataLiveInfoForRoute?> { response ->
        try {
            val parser = LiveInfoForRouteParser()
            val xmlParser = SAXParserFactory.newInstance().newSAXParser().xmlReader
            xmlParser.contentHandler = parser
            xmlParser.parse(InputSource(response.charStream()))
            parser.data
        } catch (e: Exception) {
            null
        }
    }
}