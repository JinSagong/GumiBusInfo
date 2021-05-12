package com.jin.businfo_gumi.model.repository

import com.jin.businfo_gumi.model.data.DataLiveInfoForStation
import com.jin.businfo_gumi.model.data.DataLiveItemForStation
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler

class LiveInfoForStationParser : DefaultHandler() {
    private var resultCode = ""
    private var resultMsg = ""
    private var items = arrayListOf<DataLiveItemForStation>()
    val data get() = DataLiveInfoForStation(resultCode, resultMsg, items)

    private var routeId = ""
    private var routeNo = ""
    private var arrTime = ""
    private var prevStationCount = ""
    private var routeType = ""
    private var vehicleType = ""

    private var parsingResultCode = false
    private var parsingResultMsg = false
    private var parsingRouteId = false
    private var parsingRouteNo = false
    private var parsingArrTime = false
    private var parsingPrevStationCount = false
    private var parsingRouteType = false
    private var parsingVehicleType = false

    // 태그의 시작(<tag>)을 발견할 때 호출됨
    @Throws(SAXException::class)
    override fun startElement(
        uri: String?, localName: String?, qName: String?, attributes: Attributes?
    ) {
        when (localName) {
            "resultCode" -> {
                parsingResultCode = true
                resultCode = ""
            }
            "resultMsg" -> {
                parsingResultMsg = true
                resultMsg = ""
            }
            "item" -> {
                routeId = ""
                routeNo = ""
                arrTime = ""
                prevStationCount = ""
                routeType = ""
                vehicleType = ""
            }
            "routeid" -> {
                parsingRouteId = true
                routeId = ""
            }
            "routeno" -> {
                parsingRouteNo = true
                routeNo = ""
            }
            "arrtime" -> {
                parsingArrTime = true
                arrTime = ""
            }
            "arrprevstationcnt" -> {
                parsingPrevStationCount = true
                prevStationCount = ""
            }
            "routetp" -> {
                parsingRouteType = true
                routeType = ""
            }
            "vehicletp" -> {
                parsingVehicleType = true
                vehicleType = ""
            }
        }
        super.startElement(uri, localName, qName, attributes)
    }

    // 태그의 끝(</tag>)을 발견할 때 호출됨
    @Throws(SAXException::class)
    override fun endElement(uri: String?, localName: String?, qName: String?) {
        when (localName) {
            "resultCode" -> parsingResultCode = false
            "resultMsg" -> parsingResultMsg = false
            "item" -> items.add(
                DataLiveItemForStation(
                    routeId,
                    routeNo,
                    arrTime.toIntOrNull(),
                    prevStationCount.toIntOrNull(),
                    routeType,
                    vehicleType
                )
            )
            "routeid" -> parsingRouteId = false
            "routeno" -> parsingRouteNo = false
            "arrtime" -> parsingArrTime = false
            "arrprevstationcnt" -> parsingPrevStationCount = false
            "routetp" -> parsingRouteType = false
            "vehicletp" -> parsingVehicleType = false
        }
        super.endElement(uri, localName, qName)
    }

    // 태그의 시작(<tag>)과 끝(</tag>) 사이의 값을 읽어옴
    @Throws(SAXException::class)
    override fun characters(ch: CharArray?, start: Int, length: Int) {
        val buff = ch?.let { String(ch, start, length) } ?: ""
        when {
            parsingResultCode -> resultCode += buff
            parsingResultMsg -> resultMsg += buff
            parsingRouteId -> routeId += buff
            parsingRouteNo -> routeNo += buff
            parsingArrTime -> arrTime += buff
            parsingPrevStationCount -> prevStationCount += buff
            parsingRouteType -> routeType += buff
            parsingVehicleType -> vehicleType += buff
        }
    }
}