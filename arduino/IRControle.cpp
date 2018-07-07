#include "IRControle.hpp"

IRControle::IRControle()
{
  
}

void IRControle::ligar()
{
  this->env_cod(this->ligar_cod);
}
	
void IRControle::desligar()
{
  this->env_cod(this->desli_cod);
}
	
void IRControle::alt_temp(int temp)
{
  if ((temp < min_temp) || (temp > max_temp))
  {
    return;
  }
  
  int i = temp - min_temp;
  this->env_cod(temp_cod[i]);
}
	
void IRControle::env_cod(unsigned long cod)
{
//  Serial.println(cod);
//  irsend.sendNEC(cod, this->nbits);
//  for (int i = 0; i < 3; i++) {
//    irsend.sendRC5(cod, this->nbits);
//    delay(200);
//    irsend.sendRC6(cod, this->nbits);
//    delay(200);
//    irsend.sendNEC(cod,this->nbits);
//    delay(200);
//  }
   for (int i = 0; i < 3; i++) {
    irsend.sendRC5(cod + 0x7FFFC00, 32);
    delay(200);
    irsend.sendRC6(cod + 0x7FFFC00, 32);
    delay(200);
    irsend.sendNEC(cod + 0x7FFFC00, 32);
    delay(200);
  }
  delay(1000);
}

