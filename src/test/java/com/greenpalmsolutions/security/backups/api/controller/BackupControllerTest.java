package com.greenpalmsolutions.security.backups.api.controller;

import com.greenpalmsolutions.security.backups.api.behavior.UploadBackups;
import com.greenpalmsolutions.security.backups.api.behavior.DownloadBackup;
import com.greenpalmsolutions.security.backups.api.model.BackupDetails;
import com.greenpalmsolutions.security.backups.api.model.UploadBackupsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BackupControllerTest {

    @Mock
    private UploadBackups uploadBackups;

    @Mock
    private DownloadBackup downloadBackup;

    private MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new BackupController(uploadBackups, downloadBackup))
                .build();
    }

    @Test
    void createsTheBackups() throws Exception {
        UploadBackupsResponse theResponse = new UploadBackupsResponse(444);

        when(uploadBackups.uploadBackupsForRequest(any())).thenReturn(theResponse);

        MockMultipartFile file1 = new MockMultipartFile(
                "files", "file1.txt", "text/plain", "Hello, World!".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
                "files", "file2.txt", "text/plain", "Another file.".getBytes());

        mockMvc.perform(multipart("/v1/backups")
                        .file(file1)
                        .file(file2))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.formattedSize", is("444 bytes")));
    }

    @Test
    void findsTheBackup() throws Exception {
        BackupDetails theDetails = new BackupDetails("someone.txt", "data".getBytes());

        when(downloadBackup.downloadForRequest(any())).thenReturn(theDetails);

        mockMvc.perform(get("/v1/backups")
                        .param("file-name", "someone.txt"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=someone.txt"))
                .andExpect(content().bytes("data".getBytes()));
    }
}