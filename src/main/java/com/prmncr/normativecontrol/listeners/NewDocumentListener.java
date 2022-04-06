package com.prmncr.normativecontrol.listeners;

import com.prmncr.normativecontrol.dtos.ProcessedDocument;
import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.events.NewDocumentEvent;
import com.prmncr.normativecontrol.services.DocumentHandler;
import com.prmncr.normativecontrol.services.DocumentRepository;
import com.prmncr.normativecontrol.services.DocumentStorage;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NewDocumentListener {
    private final DocumentRepository documentRepository;
    private final DocumentStorage documentStorage;
    private final DocumentHandler documentHandler;

    public NewDocumentListener(DocumentRepository documentRepository,
                               DocumentStorage documentStorage,
                               DocumentHandler documentHandler) {
        this.documentRepository = documentRepository;
        this.documentStorage = documentStorage;
        this.documentHandler = documentHandler;
    }

    @Async
    @EventListener
    public void handleDocument(NewDocumentEvent event) {
        var document = documentStorage.getById(event.getDocumentId());
        document.state = State.PROCESSING;
        documentHandler.handle(document);
        document.state = State.READY;
        documentRepository.save(new ProcessedDocument(document.getId(), document.getFile()));
    }
}
