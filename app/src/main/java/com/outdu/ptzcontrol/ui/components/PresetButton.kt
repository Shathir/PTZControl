package com.outdu.ptzcontrol.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.outdu.ptzcontrol.R
import kotlinx.coroutines.selects.select


@Composable
fun PresetRow(){


    var isSelected by remember { mutableStateOf(0) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    )
    {

        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            items(10) { i ->
                Box(
                    modifier = Modifier.width(100.dp)
                        .height(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if(isSelected == i) Color.Black else Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = Color(0xFF737373),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            isSelected = i
                        },
                    contentAlignment = Alignment.Center
                )
                {
                    Text(
                        text = "Preset $i",
                        style = TextStyle(
                            color = if(isSelected == i) Color.White else Color(0xFF2A2A2A),
                            fontSize = 14.sp,
                            fontWeight = FontWeight(400),
                            fontFamily = FontFamily.SansSerif
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Box(

            modifier = Modifier.size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Transparent)
                .border(
                    width = 1.dp,
                    color = Color(0xFF737373),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        )
        {
            Icon(
                painter = painterResource(R.drawable.plus),
                contentDescription = "Add",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
                    .align(Alignment.Center)
            )
        }

    }
}