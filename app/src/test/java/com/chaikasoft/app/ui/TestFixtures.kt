package com.chaikasoft.app.ui

import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.ConductorDomain
import com.chaikasoft.app.domain.models.FastReportDomain
import com.chaikasoft.app.domain.models.PackageItemDomain
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import java.time.LocalDateTime

internal fun station(
    code: String = "100",
    name: String = "Moscow",
    city: String = "Moscow"
): StationDomain = StationDomain(code = code, name = name, city = city)

internal fun trip(
    uuid: String = "trip-1",
    from: StationDomain = station(code = "100", name = "From"),
    to: StationDomain = station(code = "200", name = "To")
): TripDomain = TripDomain(
    uuid = uuid,
    trainNumber = "A-100",
    departure = "2026-01-01T10:00:00+03:00",
    arrival = "2026-01-01T12:00:00+03:00",
    duration = "PT2H",
    from = from,
    to = to
)

internal fun carriage(number: String = "1", classType: String = "Standard"): CarriageDomain =
    CarriageDomain(carNumber = number, classType = classType)

internal fun shift(
    trip: TripDomain = trip(),
    carriage: CarriageDomain? = carriage(),
    status: TripShiftStatusDomain = TripShiftStatusDomain.ACTIVE
): ConductorTripShiftDomain = ConductorTripShiftDomain(
    trip = trip,
    activeCarriage = carriage,
    status = status
)

internal fun productInfo(
    id: Int = 1,
    name: String = "Tea",
    description: String = "Black tea",
    image: String = "img",
    price: Int = 100
): ProductInfoDomain = ProductInfoDomain(
    id = id,
    name = name,
    description = description,
    image = image,
    price = price
)

internal fun cartItem(
    product: ProductInfoDomain = productInfo(),
    quantity: Int = 1
): CartItemDomain = CartItemDomain(product = product, quantity = quantity)

internal fun packageItem(
    product: ProductInfoDomain = productInfo(),
    quantity: Int = 10
): PackageItemDomain = PackageItemDomain(productInfoDomain = product, currentQuantity = quantity)

internal fun report(
    price: Int,
    soldCash: Int
): FastReportDomain = FastReportDomain(
    productName = "X",
    productPrice = price,
    addedQuantity = 0,
    replenishedQuantity = 0,
    soldCashQuantity = soldCash,
    soldCartQuantity = 0,
    revenue = price * soldCash
)

internal fun conductor(id: Int? = 1): ConductorDomain = ConductorDomain(
    id = id,
    name = "Ivan",
    familyName = "Ivanov",
    givenName = "Ivanovich",
    employeeID = "123",
    image = "img"
)

internal fun validLocalDateTime(): LocalDateTime = LocalDateTime.of(2026, 1, 1, 10, 0)

