package com.jin.businfo_gumi.model.repository

import com.jin.businfo_gumi.model.data.DataLiveInfoForStation
import okhttp3.ResponseBody
import org.xml.sax.InputSource
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import javax.xml.parsers.SAXParserFactory

class XmlConverterFactoryForStation : Converter.Factory() {
    companion object {
        fun create() = XmlConverterFactoryForStation()
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ) = Converter<ResponseBody, DataLiveInfoForStation?> { response ->
        try {
            val parser = LiveInfoForStationParser()
            val xmlParser = SAXParserFactory.newInstance().newSAXParser().xmlReader
            xmlParser.contentHandler = parser
            xmlParser.parse(InputSource(response.charStream()))
            parser.data
        } catch (e: Exception) {
            null
        }
    }
}