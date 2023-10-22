package com.driver.services.impl;

import com.driver.model.TripBooking;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.model.TripStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		Optional<Customer> customerOptional = customerRepository2.findById(customerId);
		if (customerOptional.isPresent()){
			customerRepository2.delete(customerOptional.get());
		}
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query

		List<Driver> drivers = driverRepository2.findAll();

		Driver driver = null;

		for (Driver x:drivers){
			if (x.getCab().getAvailable()){
				//Means when we dont have driver and the fixed driver entry driver id is more than that of current one
				if (driver == null || driver.getDriverId() >x.getDriverId()){
					driver = x;
				}
			}
		}

		//Even then if we dont have any free driver with lowest driverId and have cab available

		if (driver == null){
			throw new Exception("No cab Available!");
		}

		TripBooking tripBooking = new TripBooking();
		Optional<Customer> customerOptional = customerRepository2.findById(customerId);
		Customer customer = customerOptional.get();
		tripBooking.setCustomer(customer);
		tripBooking.setDriver(driver);
		tripBooking.setStatus(TripStatus.CONFIRMED);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setBill(driver.getCab().getPerKmRate() * distanceInKm);
		tripBooking.getDriver().getCab().setAvailable(false);

		tripBookingRepository2.save(tripBooking);

		driver.getTripBookingList().add(tripBooking);
		customer.getTripBookingList().add(tripBooking);

		driverRepository2.save(driver);
		customerRepository2.save(customer);
		return tripBooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> optionalTripBooking = tripBookingRepository2.findById(tripId);

		if (optionalTripBooking.isPresent()){
			TripBooking tripBooking = optionalTripBooking.get();
			tripBooking.setStatus(TripStatus.CANCELED);
			tripBooking.getDriver().getCab().setAvailable(true);
			tripBooking.setBill(0);
			tripBookingRepository2.save(tripBooking);
		}
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> optionalTripBooking = tripBookingRepository2.findById(tripId);
		if (optionalTripBooking.isPresent()){
			TripBooking tripBooking = optionalTripBooking.get();
			tripBooking.setStatus(TripStatus.COMPLETED);
			tripBooking.getDriver().getCab().setAvailable(true);
			tripBookingRepository2.save(tripBooking);
		}
	}
}
