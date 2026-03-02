CREATE TABLE legal_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id UUID NOT NULL,
    file_attachment_id UUID,
    updated_at TIMESTAMPTZ DEFAULT now(),
    created_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT fk_legal_doc_owner FOREIGN KEY (owner_id) REFERENCES persons(id),
    CONSTRAINT fk_legal_doc_attachment FOREIGN KEY (file_attachment_id) REFERENCES media_assets(id)
);
