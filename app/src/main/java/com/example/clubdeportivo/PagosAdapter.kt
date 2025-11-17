package com.example.clubdeportivo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PagosAdapter(private val pagos: List<Map<String, String>>) : RecyclerView.Adapter<PagosAdapter.PagoViewHolder>() {

    class PagoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dniTextView: TextView = view.findViewById(R.id.pago_dni)
        val tipoSocioTextView: TextView = view.findViewById(R.id.pago_tipo_socio)
        val fechaTextView: TextView = view.findViewById(R.id.pago_fecha)
        val montoTextView: TextView = view.findViewById(R.id.pago_monto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pago, parent, false)
        return PagoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PagoViewHolder, position: Int) {
        val pago = pagos[position]
        holder.dniTextView.text = "DNI: ${pago["dni"]}"
        holder.tipoSocioTextView.text = if (pago["tipo"] == "socio") "Socio" else "No Socio"
        holder.fechaTextView.text = "Fecha: ${pago["fecha"]}"
        holder.montoTextView.text = "Monto: $${pago["monto"]}"
    }

    override fun getItemCount() = pagos.size
}
