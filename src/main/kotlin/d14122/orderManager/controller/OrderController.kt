package d14122.orderManager.controller

import d14122.orderManager.database.OrderEntity
import d14122.orderManager.database.OrderRepository
import d14122.orderManager.model.Flavor
import d14122.orderManager.model.Item
import d14122.orderManager.model.Order
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.http.MediaType
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
@EnableAutoConfiguration
@EnableScheduling
class OrderController(@Autowired val repo: OrderRepository) {

    @Autowired
    private lateinit var smt: SimpMessagingTemplate

    private val stockQueMap = mutableMapOf<String, Queue<Date>>()
    private final val flavorList = listOf("sugar", "cocoa", "kinako", "cinnammon", "saltAndPepper")
    private val shortageQueMap = mutableMapOf<String, Queue<Date>>()

    init {
        flavorList.forEach { x -> stockQueMap[x] = ArrayDeque<Date>()}
        flavorList.forEach { x -> shortageQueMap[x] = ArrayDeque<Date>()}
    }

    @Scheduled(fixedRate = 10000)
    fun request() {
        smt.convertAndSend("/topic/request", mapToList(shortageQueMap))
    }

    @Scheduled(fixedRate = 10000)
    fun delivery() {
        smt.convertAndSend("/topic/delivery", mapToList(stockQueMap))
    }

    fun delivery(flavor: Flavor) {
        smt.convertAndSend("/topic/delivery", flavor)
    }

    @PostMapping(value = ["/kitchen"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun cook(@Validated @RequestBody flavor: Flavor): List<Order> {
        val flavorName = flavor.flavor
        if (flavorList.contains(flavorName)) {
            if (shortageQueMap[flavorName]!!.poll() == null) {
                stockQueMap[flavorName]!!.offer(Date())
                delivery()
            } else {
                delivery(flavor)
            }
        }
        return mapToList(shortageQueMap)
    }

    @PostMapping(value = ["/reception"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun order(@Validated @RequestBody flavor: Flavor): List<Order> {
        val flavorName = flavor.flavor
        if (flavorList.contains(flavorName)) {
            repo.save(OrderEntity(Order(Item(flavorName, 100.0), Date())))
            if (stockQueMap[flavorName]!!.poll() == null) {
                shortageQueMap[flavorName]!!.offer(Date())
                request()
            }
        }
        return mapToList(stockQueMap)
    }

    private fun mapToList(map: Map<String, Queue<Date>>): List<Order> {
        val list = mutableListOf<Order>()
        map.keys.forEach { x ->
            map[x]!!.forEach {
                list.add(Order(Item(x, 100.0), it))
            }
        }
        list.sortBy { it.date }
        list.reverse()
        return list
    }

    @GetMapping("/record")
    fun record(modelMap: ModelMap): String {
        modelMap.addAttribute("recordList", repo.findAll())
        return "/record"
    }

}