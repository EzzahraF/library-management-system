package com.ensam.library.service;

import com.ensam.library.dto.MemberDTO;
import com.ensam.library.model.Member;
import com.ensam.library.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Member Service Tests")
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member testMember1;
    private Member testMember2;
    private MemberDTO testMemberDTO;

    @BeforeEach
    void setUp() {
        testMember1 = new Member(
                1L,
                "John Doe",
                "123 Main St, City",
                "john.doe@email.com",
                "+1234567890"
        );

        testMember2 = new Member(
                2L,
                "Jane Smith",
                "456 Oak Ave, Town",
                "jane.smith@email.com",
                "+0987654321"
        );

        testMemberDTO = new MemberDTO(
                null,
                "Bob Johnson",
                "789 Pine Rd, Village",
                "bob.johnson@email.com",
                "+1122334455"
        );
    }

    @Test
    @DisplayName("Should return all members")
    void testGetAllMembers() {
        // Given
        List<Member> expectedMembers = Arrays.asList(testMember1, testMember2);
        when(memberRepository.findAll()).thenReturn(expectedMembers);

        // When
        List<Member> result = memberService.getAllMembers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Smith", result.get(1).getName());
        verify(memberRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return member by ID when member exists")
    void testGetMemberById_Success() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember1));

        // When
        Optional<Member> result = memberService.getMemberById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        assertEquals("john.doe@email.com", result.get().getEmail());
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when member does not exist")
    void testGetMemberById_NotFound() {
        // Given
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Member> result = memberService.getMemberById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(memberRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create a new member successfully")
    void testCreateMember_Success() {
        // Given
        Member savedMember = new Member(
                1L,
                "Bob Johnson",
                "789 Pine Rd, Village",
                "bob.johnson@email.com",
                "+1122334455"
        );
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        // When
        Member result = memberService.createMember(testMemberDTO);

        // Then
        assertNotNull(result);
        assertEquals("Bob Johnson", result.getName());
        assertEquals("bob.johnson@email.com", result.getEmail());
        assertEquals("+1122334455", result.getPhoneNumber());
        verify(memberRepository, times(1)).findByEmail("bob.johnson@email.com");
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testCreateMember_EmailAlreadyExists() {
        // Given
        when(memberRepository.findByEmail("bob.johnson@email.com"))
                .thenReturn(Optional.of(testMember1));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> memberService.createMember(testMemberDTO)
        );

        assertEquals("Un membre avec cet email existe déjà", exception.getMessage());
        verify(memberRepository, times(1)).findByEmail("bob.johnson@email.com");
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("Should update member when member exists and email is unique")
    void testUpdateMember_Success() {
        // Given
        MemberDTO updateDTO = new MemberDTO(
                1L,
                "John Updated",
                "999 New St",
                "john.updated@email.com",
                "+9999999999"
        );
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember1));
        when(memberRepository.findByEmail("john.updated@email.com")).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Member> result = memberService.updateMember(1L, updateDTO);

        // Then
        assertTrue(result.isPresent());
        assertEquals("John Updated", result.get().getName());
        assertEquals("john.updated@email.com", result.get().getEmail());
        assertEquals("+9999999999", result.get().getPhoneNumber());
        verify(memberRepository, times(1)).findById(1L);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("Should update member with same email")
    void testUpdateMember_SameEmail() {
        // Given
        MemberDTO updateDTO = new MemberDTO(
                1L,
                "John Updated",
                "999 New St",
                "john.doe@email.com",  // Same email
                "+9999999999"
        );
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember1));
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Member> result = memberService.updateMember(1L, updateDTO);

        // Then
        assertTrue(result.isPresent());
        assertEquals("John Updated", result.get().getName());
        verify(memberRepository, times(1)).findById(1L);
        verify(memberRepository, never()).findByEmail(anyString()); // Email check skipped
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("Should throw exception when updating with existing email")
    void testUpdateMember_EmailAlreadyUsed() {
        // Given
        MemberDTO updateDTO = new MemberDTO(
                1L,
                "John Updated",
                "999 New St",
                "jane.smith@email.com",  // Email of another member
                "+9999999999"
        );
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember1));
        when(memberRepository.findByEmail("jane.smith@email.com")).thenReturn(Optional.of(testMember2));

        // When & Then
        assertThrows(
                IllegalArgumentException.class,
                () -> memberService.updateMember(1L, updateDTO)
        );

        verify(memberRepository, times(1)).findById(1L);
        verify(memberRepository, times(1)).findByEmail("jane.smith@email.com");
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("Should return empty when updating non-existent member")
    void testUpdateMember_NotFound() {
        // Given
        MemberDTO updateDTO = new MemberDTO(999L, "Test", "Test", "test@test.com", "+1111111111");
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Member> result = memberService.updateMember(999L, updateDTO);

        // Then
        assertFalse(result.isPresent());
        verify(memberRepository, times(1)).findById(999L);
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("Should delete member when member exists")
    void testDeleteMember_Success() {
        // Given
        when(memberRepository.existsById(1L)).thenReturn(true);
        doNothing().when(memberRepository).deleteById(1L);

        // When
        boolean result = memberService.deleteMember(1L);

        // Then
        assertTrue(result);
        verify(memberRepository, times(1)).existsById(1L);
        verify(memberRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should return false when deleting non-existent member")
    void testDeleteMember_NotFound() {
        // Given
        when(memberRepository.existsById(999L)).thenReturn(false);

        // When
        boolean result = memberService.deleteMember(999L);

        // Then
        assertFalse(result);
        verify(memberRepository, times(1)).existsById(999L);
        verify(memberRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should find member by email")
    void testGetMemberByEmail_Success() {
        // Given
        when(memberRepository.findByEmail("john.doe@email.com"))
                .thenReturn(Optional.of(testMember1));

        // When
        Optional<Member> result = memberService.getMemberByEmail("john.doe@email.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        assertEquals("john.doe@email.com", result.get().getEmail());
        verify(memberRepository, times(1)).findByEmail("john.doe@email.com");
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void testGetMemberByEmail_NotFound() {
        // Given
        when(memberRepository.findByEmail("nonexistent@email.com"))
                .thenReturn(Optional.empty());

        // When
        Optional<Member> result = memberService.getMemberByEmail("nonexistent@email.com");

        // Then
        assertFalse(result.isPresent());
        verify(memberRepository, times(1)).findByEmail("nonexistent@email.com");
    }
}
