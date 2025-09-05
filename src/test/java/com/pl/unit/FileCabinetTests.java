package com.pl.unit;

import com.pl.FileCabinet;
import com.pl.Folder;
import com.pl.MultiFolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FileCabinetTests {

    private FileCabinet fileCabinet;

    @BeforeEach
    void setUp() {
        Folder mockFolderMedium = mock(Folder.class);
        when(mockFolderMedium.getName()).thenReturn("TestFolderMedium");
        when(mockFolderMedium.getSize()).thenReturn("MEDIUM");

        Folder mockFolderMediumNested = mock(Folder.class);
        when(mockFolderMediumNested.getName()).thenReturn("TestFolderMediumNested");
        when(mockFolderMediumNested.getSize()).thenReturn("MEDIUM");

        Folder mockFolderLargeNested = mock(Folder.class);
        when(mockFolderLargeNested.getName()).thenReturn("TestFolderLargeNested");
        when(mockFolderLargeNested.getSize()).thenReturn("LARGE");

        MultiFolder mockMultiFolder = mock(MultiFolder.class);
        when(mockMultiFolder.getName()).thenReturn("TestMultiFolder");
        when(mockMultiFolder.getSize()).thenReturn("LARGE");
        when(mockMultiFolder.getFolders()).thenReturn(java.util.List.of(mockFolderMediumNested, mockFolderLargeNested));

        fileCabinet = new FileCabinet(java.util.List.of(mockFolderMedium, mockMultiFolder));
    }

    @Test
    void shouldFindFolderByName_WhenExists() {
        var result = fileCabinet.findFolderByName("TestFolderMedium");

        assertTrue(result.isPresent());
        assertEquals("TestFolderMedium", result.get().getName());
    }

    @Test
    void shouldFindNestedFolderByName_WhenExists() {
        var result = fileCabinet.findFolderByName("TestFolderMediumNested");

        assertTrue(result.isPresent());
        assertEquals("TestFolderMediumNested", result.get().getName());
    }

    @Test
    void shouldNotFindFolderByName_WhenNotExists() {
        var result = fileCabinet.findFolderByName("NonExistentFolder");

        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindFoldersBySize_WhenExists() {
        var result = fileCabinet.findFoldersBySize("LARGE");

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(folder -> folder.getName().equals("TestMultiFolder")));
        assertTrue(result.stream().anyMatch(folder -> folder.getName().equals("TestFolderLargeNested")));
    }

    @Test
    void shouldReturnException_WhenWrongSizeProvided() {
        assertThrows(RuntimeException.class, () -> fileCabinet.findFoldersBySize("NON_EXISTENT_SIZE"));
    }

    @Test
    void shouldReturnEmptyList_WhenNoFoldersOfExpectedSize() {
        var result = fileCabinet.findFoldersBySize("SMALL");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCountAllFoldersInCabinet() {
        int count = fileCabinet.count();

        assertEquals(4, count);
    }
}
