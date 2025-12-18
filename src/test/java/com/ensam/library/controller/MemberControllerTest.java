package com.ensam.library.controller;

import com.ensam.library.dto.MemberDTO;
import com.ensam.library.model.Member;
import com.ensam.library.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(MemberController.class)
@DisplayName("Member Controller Tests")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    private Member testMember;
    private MemberDTO testMemberDTO;

    @BeforeEach
    void setUp() {
        testMember = new Member(1L, "John Doe", "123 Main St", "john@test.com", "+1234567890");
        testMemberDTO = new MemberDTO(null, "John Doe", "123 Main St", "john@test.com", "+1234567890");
    }

    @Test
    @DisplayName("GET /api/members - Should return all members")
    void testGetAllMembers() throws Exception {
        // Given
        List<Member> members = Arrays.asList(testMember);
        when(memberService.getAllMembers()).thenReturn(members);

        // When & Then
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john@test.com"));

        verify(memberService, times(1)).getAllMembers();
    }

    @Test
    @DisplayName("GET /api/members/{id} - Should return member when exists")
    void testGetMemberById_Success() throws Exception {
        // Given
        when(memberService.getMemberById(1L)).thenReturn(Optional.of(testMember));

        // When & Then
        mockMvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(memberService, times(1)).getMemberById(1L);
    }

    @Test
    @DisplayName("GET /api/members/{id} - Should return 404 when member not found")
    void testGetMemberById_NotFound() throws Exception {
        // Given
        when(memberService.getMemberById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/members/999"))
                .andExpect(status().isNotFound());

        verify(memberService, times(1)).getMemberById(999L);
    }

    @Test
    @DisplayName("POST /api/members - Should create member successfully")
    void testCreateMember_Success() throws Exception {
        // Given
        when(memberService.createMember(any(MemberDTO.class))).thenReturn(testMember);

        // When & Then
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMemberDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(memberService, times(1)).createMember(any(MemberDTO.class));
    }

    @Test
    @DisplayName("POST /api/members - Should return 400 when email is invalid")
    void testCreateMember_InvalidEmail() throws Exception {
        // Given
        MemberDTO invalidDTO = new MemberDTO(null, "John Doe", "123 Main St", "invalid-email", "+1234567890");

        // When & Then
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(memberService, never()).createMember(any(MemberDTO.class));
    }

    @Test
    @DisplayName("POST /api/members - Should return 400 when phone number is invalid")
    void testCreateMember_InvalidPhone() throws Exception {
        // Given
        MemberDTO invalidDTO = new MemberDTO(null, "John Doe", "123 Main St", "john@test.com", "abc");

        // When & Then
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(memberService, never()).createMember(any(MemberDTO.class));
    }

    @Test
    @DisplayName("PUT /api/members/{id} - Should update member successfully")
    void testUpdateMember_Success() throws Exception {
        // Given
        Member updatedMember = new Member(1L, "John Updated", "999 New St", "john@test.com", "+9999999999");
        when(memberService.updateMember(eq(1L), any(MemberDTO.class))).thenReturn(Optional.of(updatedMember));

        // When & Then
        mockMvc.perform(put("/api/members/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMemberDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"));

        verify(memberService, times(1)).updateMember(eq(1L), any(MemberDTO.class));
    }

    @Test
    @DisplayName("PUT /api/members/{id} - Should return 404 when member not found")
    void testUpdateMember_NotFound() throws Exception {
        // Given
        when(memberService.updateMember(eq(999L), any(MemberDTO.class))).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/members/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMemberDTO)))
                .andExpect(status().isNotFound());

        verify(memberService, times(1)).updateMember(eq(999L), any(MemberDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/members/{id} - Should delete member successfully")
    void testDeleteMember_Success() throws Exception {
        // Given
        when(memberService.deleteMember(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/members/1"))
                .andExpect(status().isNoContent());

        verify(memberService, times(1)).deleteMember(1L);
    }

    @Test
    @DisplayName("DELETE /api/members/{id} - Should return 404 when member not found")
    void testDeleteMember_NotFound() throws Exception {
        // Given
        when(memberService.deleteMember(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/members/999"))
                .andExpect(status().isNotFound());

        verify(memberService, times(1)).deleteMember(999L);
    }
}
