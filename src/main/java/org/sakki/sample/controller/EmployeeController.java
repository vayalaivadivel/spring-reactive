package org.sakki.sample.controller;

import org.sakki.sample.entity.Employee;
import org.sakki.sample.entity.EmployeeEvent;
import org.sakki.sample.repo.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/rest/employee")
public class EmployeeController {
    @Autowired
    private EmployeeRepository employeeRepository;

    @PostConstruct
    public void init() {

        employeeRepository
                .deleteAll();

        final List<Employee> list = Arrays.asList(new Employee(
                "Peter", 23000L), new Employee(
                "Sam", 13000L), new Employee(
                "Ryan", 20000L), new Employee(
                "Chris", 53000L));

        for (final Employee e : list) {
            employeeRepository.save(e);
        }

    }
    @GetMapping("/all")
    public List<Employee> getAll() {
        return employeeRepository
                .findAll();
    }
    @GetMapping(value = "/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<EmployeeEvent> getEvents(@PathVariable("id") final Integer empId) {
        final Optional<Employee> employee = employeeRepository.findById(empId);
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(2));
        Flux<EmployeeEvent> employeeEventFlux = Flux.fromStream(
                Stream.generate(() -> new EmployeeEvent(employee.get(),
                        new Date()))
        );


        return Flux.zip(interval, employeeEventFlux)
                .map(Tuple2::getT2);
    }
}