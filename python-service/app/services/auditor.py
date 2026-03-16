import pandas as pd
import io

class AuditorService:
    @staticmethod
    def run_audit(file_contents: bytes):
        # Carregando o arquivo Excel/CSV da memória
        df = pd.read_excel(io.BytesIO(file_contents))
        
        # Padronizando colunas (evita erros de espaços ou maiúsculas)
        df.columns = [c.strip().lower() for c in df.columns]

        # --- Lógica de Auditoria Exemplo ---
        # Digamos que a coluna 'valor_cobrado' deve ser igual a 'valor_esperado'
        # Se houver divergência, marcamos como inconsistência.
        
        if 'valor_cobrado' in df.columns and 'valor_esperado' in df.columns:
            df['divergencia'] = df['valor_cobrado'] - df['valor_esperado']
            inconsistencias = df[df['divergencia'] != 0].copy()
            
            resumo = {
                "total_linhas": len(df),
                "total_inconsistencias": len(inconsistencias),
                "valor_total_divergente": float(inconsistencias['divergencia'].sum()),
                "detalhes": inconsistencias.to_dict(orient='records')
            }
        else:
            # Caso o arquivo não tenha o formato esperado
            resumo = {
                "total_linhas": len(df),
                "aviso": "Colunas de auditoria não encontradas. Realizado apenas contagem.",
                "total_valor": float(df.iloc[:, -1].sum()) if not df.empty else 0
            }

        return resumo