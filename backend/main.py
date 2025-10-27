from fastapi import FastAPI
from pydantic import BaseModel
from typing import List, Optional

app = FastAPI(title="SAVI API", version="0.0.1")

class Measurement(BaseModel):
    ts: int
    steps: Optional[int] = None
    hr: Optional[int] = None

class IngestPayload(BaseModel):
    user_id: str
    items: List[Measurement]

@app.get("/health")
def health():
    return {"ok": True}

@app.post("/v1/ingest")
def ingest(payload: IngestPayload):
    return {"received": len(payload.items)}
