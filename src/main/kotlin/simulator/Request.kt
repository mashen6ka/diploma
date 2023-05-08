package simulator

import time.Time

//class Request(private val timeIn: Time) {
//    public var timeOut: Time = 0
//        set(value) {
//            require(timeOut > timeIn) { "timeOut should be" }
//            field = value
//        }
//}
//

data class Request(val timeIn: Time) {
    var timeOut: Time = 0
}