from fastapi import FastAPI, UploadFile
import pandas as pd

app = FastAPI()

@app.get("/")
def health():
    return {"status": "running"}

@app.post("/audit")
async def audit_file(file: UploadFile):

    df = pd.read_excel(file.file)

    total = df["valor"].sum()

    return {
        "linhas": len(df),
        "valor_total": float(total)
    }