package org.egov.sr.web.controllers;

import lombok.extern.slf4j.Slf4j;

import org.egov.sr.service.MigrationService;
import org.egov.sr.web.models.srV1.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/migration")
@Slf4j
public class MigrationController {


    @Autowired
    private MigrationService migrationService;


    @RequestMapping(value="/_transform", method = RequestMethod.POST)
    public ResponseEntity<Map> requestsCreatePost(@Valid @RequestBody ServiceResponse request) throws IOException {

        Map<String, Object> response = migrationService.migrate(request);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
