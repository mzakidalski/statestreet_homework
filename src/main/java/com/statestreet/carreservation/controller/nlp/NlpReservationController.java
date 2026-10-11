package com.statestreet.carreservation.controller.nlp;

import com.statestreet.carreservation.service.nlp.NlpReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nlpReservation")
public class NlpReservationController {
    private final NlpReservationService service;
    private final NlpReservationMapper mapper;

    public NlpReservationController(NlpReservationService service, NlpReservationMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<NlpReservationDto> makeReservation(@RequestBody NlpReservationRequestDto request) {
        return ResponseEntity.ok(mapper.toDto(service.makeReservation(request.getUserId(), request.getText())));
    }
}
