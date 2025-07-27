package com.statestreet.carreservation.controller.reservartion;

import com.statestreet.carreservation.service.reservation.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/reservations")
public class ReservationController {
    @Autowired
    ReservationService service;

    @Autowired
    ReservationMapper mapper;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReservationDto>> listByUser(
            @PathVariable int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageReq = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ReservationDto> dtoPage = service.findByUserId(userId, pageReq)
                .map(reservation -> mapper.toDto(reservation));
        return ResponseEntity.ok(dtoPage);
    }
}
