package com.ridwantaufik.allobank.service;

import com.ridwantaufik.allobank.dto.CreateBillGroupRequest;
import com.ridwantaufik.allobank.dto.CreateBillGroupResponse;
import com.ridwantaufik.allobank.dto.ParticipantResponse;
import com.ridwantaufik.allobank.entity.BillGroup;
import com.ridwantaufik.allobank.entity.Participant;
import com.ridwantaufik.allobank.repository.BillGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BillGroupService {

    private final BillGroupRepository billGroupRepository;

    public BillGroupService(BillGroupRepository billGroupRepository) {
        this.billGroupRepository = billGroupRepository;
    }

    @Transactional
    public CreateBillGroupResponse createGroup(
            CreateBillGroupRequest request) {
        BillGroup billGroup = new BillGroup();
        billGroup.setName(request.name().trim());

        for (String participantName : request.participants()) {
            Participant participant = new Participant();

            participant.setName(participantName.trim());
            participant.setBillGroup(billGroup);

            billGroup.getParticipants().add(participant);
        }

        BillGroup savedGroup = billGroupRepository.save(billGroup);

        List<ParticipantResponse> participants = savedGroup.getParticipants()
                .stream()
                .map(participant -> new ParticipantResponse(
                        participant.getId(),
                        participant.getName()))
                .toList();

        return new CreateBillGroupResponse(
                savedGroup.getId(),
                savedGroup.getName(),
                participants,
                savedGroup.getCreatedAt());
    }
}