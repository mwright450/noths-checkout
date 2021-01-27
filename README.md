###### NOTHS Checkout Test

Built with gradle. 

To build:

`./gradlew clean build`

To test

`./gradlew clean build`

The main logic is applied in the class: 

`com.notonthehighstreet.service.Checkout`

The design of the solution is to use a promotion code application pipeline with the
`Checkout` class unaware of the `Promotion` implementation details. This allows the
addition of new `Promtions` by creating a new implementation of the interface and
passing the new class in the `List<Promotion>` supplied to the `Checkout` class.

Integration test for the scenarios listed in the test document:

`com.notonthehighstreet.CheckoutItTest`

