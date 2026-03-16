from fastapi import FastAPI, UploadFile, File, HTTPException
from app.services.auditor import AuditorService

app = FastAPI(title="Audit Engine Python")

@app.post("/audit")
async def audit_endpoint(file: UploadFile = File(...)):
    # Validação robusta: garante que o arquivo tem nome e a extensão correta
    filename = file.filename or "" # Se for None, vira uma string vazia
    
    if not filename.lower().endswith(('.xlsx', '.xls', '.csv')):
        raise HTTPException(
            status_code=400, 
            detail="Formato de arquivo inválido. Use Excel ou CSV."
        )

    try:
        contents = await file.read()
        resultado = AuditorService.run_audit(contents)
        return resultado
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro no processamento: {str(e)}")