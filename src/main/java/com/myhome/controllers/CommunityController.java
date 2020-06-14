/*
 * Copyright 2020 Prathab Murugan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.myhome.controllers;

import com.myhome.controllers.mapper.CommunityApiMapper;
import com.myhome.controllers.request.AddCommunityAdminRequest;
import com.myhome.controllers.request.AddCommunityHouseRequest;
import com.myhome.controllers.request.CreateCommunityRequest;
import com.myhome.controllers.response.AddCommunityAdminResponse;
import com.myhome.controllers.response.AddCommunityHouseResponse;
import com.myhome.controllers.response.CreateCommunityResponse;
import com.myhome.controllers.response.GetCommunityDetailsResponse;
import com.myhome.controllers.response.GetHouseDetailsResponse;
import com.myhome.controllers.response.ListCommunityAdminsResponse;
import com.myhome.domain.CommunityAdmin;
import com.myhome.services.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller which provides endpoints for managing community
 */
@RestController
@Slf4j
public class CommunityController {
  private final CommunityService communityService;
  private final CommunityApiMapper communityApiMapper;

  public CommunityController(
      CommunityService communityService,
      CommunityApiMapper communityApiMapper) {
    this.communityService = communityService;
    this.communityApiMapper = communityApiMapper;
  }

  @Operation(description = "Create a new community")
  @PostMapping(
      path = "/communities",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
  )
  public ResponseEntity<CreateCommunityResponse> createCommunity(@Valid @RequestBody
      CreateCommunityRequest request) {
    log.trace("Received create community request");
    var requestCommunityDto = communityApiMapper.createCommunityRequestToCommunityDto(request);
    var createdCommunity = communityService.createCommunity(requestCommunityDto);
    var createdCommunityResponse =
        communityApiMapper.communityToCreateCommunityResponse(createdCommunity);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdCommunityResponse);
  }

  @Operation(description = "List all communities which are registered")
  @GetMapping(
      path = "/communities",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
  )
  public ResponseEntity<GetCommunityDetailsResponse> listAllCommunity() {
    log.trace("Received request to list all community");
    var communityDetails = communityService.listAll();
    var communityDetailsResponse =
        communityApiMapper.communitySetToRestApiResponseCommunitySet(communityDetails);

    var response = new GetCommunityDetailsResponse();
    response.getCommunities().addAll(communityDetailsResponse);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Operation(description = "Get details about the community given a community id")
  @GetMapping(
      path = "/communities/{communityId}",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
  )
  public ResponseEntity<GetCommunityDetailsResponse> listCommunityDetails(
      @PathVariable String communityId) {
    log.trace("Received request to get details about community with id[{}]", communityId);
    var communityDetails = communityService.getCommunityDetailsById(communityId);
    var communityDetailsResponse =
        communityApiMapper.communityToRestApiResponseCommunity(communityDetails);

    var response = new GetCommunityDetailsResponse();
    response.getCommunities().add(communityDetailsResponse);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Operation(description = "List all admins of the community given a community id")
  @GetMapping(
      path = "/communities/{communityId}/admins",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
  )
  public ResponseEntity<ListCommunityAdminsResponse> listCommunityAdmins(
      @PathVariable String communityId) {
    log.trace("Received request to list all admins of community with id[{}]", communityId);
    var adminDetails = communityService.getCommunityDetailsById(communityId).getAdmins();
    var communityAdminSet =
        communityApiMapper.communityAdminSetToRestApiResponseCommunityAdminSet(adminDetails);

    var response = new ListCommunityAdminsResponse();
    response.getAdmins().addAll(communityAdminSet);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Operation(description = "List all houses of the community given a community id")
  @GetMapping(
      path = "/communities/{communityId}/houses",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
  )
  public ResponseEntity<GetHouseDetailsResponse> listCommunityHouses(
      @PathVariable String communityId) {
    log.trace("Received request to list all houses of community with id[{}]", communityId);
    var houseDetails = communityService.getCommunityDetailsById(communityId).getHouses();
    var getHouseDetailsResponseSet =
        communityApiMapper.communityHouseSetToRestApiResponseCommunityHouseSet(houseDetails);

    var response = new GetHouseDetailsResponse();
    response.setHouses(getHouseDetailsResponseSet);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Operation(description = "Add a new admin to the community given a community id")
  @PostMapping(
      path = "/communities/{communityId}/admins",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
  )
  public ResponseEntity<AddCommunityAdminResponse> addCommunityAdmin(
      @PathVariable String communityId, @Valid @RequestBody
      AddCommunityAdminRequest request) {
    log.trace("Received request to add admin to community with id[{}]", communityId);
    var community = communityService.addAdminsToCommunity(communityId, request.getAdmins());
    var response = new AddCommunityAdminResponse();
    var adminsSet =
        community.getAdmins().stream().map(CommunityAdmin::getAdminId).collect(Collectors.toSet());
    response.setAdmins(adminsSet);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(description = "Add a new house to the community given a community id")
  @PostMapping(
      path = "/communities/{communityId}/houses",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
  )
  public ResponseEntity<AddCommunityHouseResponse> addCommunityHouse(
      @PathVariable String communityId, @Valid @RequestBody
      AddCommunityHouseRequest request) {

    // TODO Replace this with bulk insert API

    log.trace("Received request to add house to community with id[{}]", communityId);

    var communityHouse = communityApiMapper.communityHouseDtoToCommunityHouse(request.getHouse());
    var houseId = communityService.addHouseToCommunity(communityId, communityHouse);
    var response = new AddCommunityHouseResponse();
    response.setHouseId(houseId);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
