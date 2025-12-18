package com.ensam.library.service;

import com.ensam.library.dto.MemberDTO;
import com.ensam.library.model.Member;
import com.ensam.library.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public List<Member> getAllMembers() {
        log.info("Récupération de tous les membres");
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(Long id) {
        log.info("Récupération du membre avec ID: {}", id);
        return memberRepository.findById(id);
    }

    @Transactional
    public Member createMember(MemberDTO memberDTO) {
        log.info("Création d'un nouveau membre: {}", memberDTO.getName());

        // Vérifier si l'email existe déjà
        if (memberRepository.findByEmail(memberDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Un membre avec cet email existe déjà");
        }

        Member member = new Member();
        member.setName(memberDTO.getName());
        member.setAddress(memberDTO.getAddress());
        member.setEmail(memberDTO.getEmail());
        member.setPhoneNumber(memberDTO.getPhoneNumber());

        return memberRepository.save(member);
    }

    @Transactional
    public Optional<Member> updateMember(Long id, MemberDTO memberDTO) {
        log.info("Mise à jour du membre avec ID: {}", id);

        return memberRepository.findById(id).map(existingMember -> {
            // Vérifier si le nouvel email n'est pas utilisé par un autre membre
            if (!existingMember.getEmail().equals(memberDTO.getEmail())) {
                memberRepository.findByEmail(memberDTO.getEmail())
                        .ifPresent(m -> {
                            throw new IllegalArgumentException("Cet email est déjà utilisé par un autre membre");
                        });
            }

            existingMember.setName(memberDTO.getName());
            existingMember.setAddress(memberDTO.getAddress());
            existingMember.setEmail(memberDTO.getEmail());
            existingMember.setPhoneNumber(memberDTO.getPhoneNumber());

            return memberRepository.save(existingMember);
        });
    }

    @Transactional
    public boolean deleteMember(Long id) {
        log.info("Suppression du membre avec ID: {}", id);
        if (memberRepository.existsById(id)) {
            memberRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Member> getMemberByEmail(String email) {
        log.info("Recherche du membre par email: {}", email);
        return memberRepository.findByEmail(email);
    }
}