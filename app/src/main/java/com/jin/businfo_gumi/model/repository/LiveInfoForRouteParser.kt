package com.jin.businfo_gumi.model.repository

import com.jin.businfo_gumi.model.data.DataLiveInfoForRoute
import com.jin.businfo_gumi.model.data.DataLiveItemForRoute
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler

class LiveInfoForRouteParser : DefaultHandler() {
    private var resultCode = ""
    private var resultMsg = ""
    private var items = arrayListOf<DataLiveItemForRoute>()
    val data get() = DataLiveInfoForRoute(resultCode, resultMsg, items)

    private var stationId = ""
    private var stationName = ""
    private var routeOrder = ""
    private var routeType = ""
    private var vehicleNo = ""
    private var lat = ""
    private var lng = ""

    private var parsingResultCode = false
    private var parsingResultMsg = false
    private var parsingStationId = false
    private var parsingStationName = false
    private var parsingRouteOrder = false
    private var parsingRouteType = false
    private var parsingVehicleNo = false
    private var parsingLat = false
    private var parsingLng = false

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
                stationId = ""
                stationName = ""
                routeOrder = ""
                routeType = ""
                vehicleNo = ""
                lat = ""
                lng = ""
            }
            "nodeid" -> {
                parsingStationId = true
                stationId = ""
            }
            "nodenm" -> {
                parsingStationName = true
                stationName = ""
            }
            "nodeord" -> {
                parsingRouteOrder = true
                routeOrder = ""
            }
            "routetp" -> {
                parsingRouteType = true
                routeType = ""
            }
            "vehicleno" -> {
                parsingVehicleNo = true
                vehicleNo = ""
            }
            "gpslati" -> {
                parsingLat = true
                lat = ""
            }
            "gpslong" -> {
                parsingLng = true
                lng = ""
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
                DataLiveItemForRoute(
                    stationId,
                    stationName,
                    routeOrder.toIntOrNull(),
                    routeType,
                    vehicleNo,
                    lat.toDoubleOrNull(),
                    lng.toDoubleOrNull()
                )
            )
            "nodeid" -> parsingStationId = false
            "nodenm" -> parsingStationName = false
            "nodeord" -> parsingRouteOrder = false
            "routetp" -> parsingRouteType = false
            "vehicleno" -> parsingVehicleNo = false
            "gpslati" -> parsingLat = false
            "gpslong" -> parsingLng = false
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
            parsingStationId -> stationId += buff
            parsingStationName -> stationName += buff
            parsingRouteOrder -> routeOrder += buff
            parsingRouteType -> routeType += buff
            parsingVehicleNo -> vehicleNo += buff
            parsingLat -> lat += buff
            parsingLng -> lng += buff
        }
    }
}