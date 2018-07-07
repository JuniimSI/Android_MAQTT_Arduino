#ifndef IRControle_hpp
#define IRControle_hpp

#include <IRremote.h>

class IRControle
{
public:
	IRControle();

	void ligar();
	void desligar();
	void alt_temp(int temp);
	void env_cod(unsigned long cod);

private:
  IRsend irsend;

//   LG
//  const int min_temp = 18;
//  const int max_temp = 30;
//	unsigned long ligar_cod  = 0x8800303;
//	unsigned long desli_cod  = 0x88C0051;
//  unsigned long temp_cod[13] = {0x880830B, 0x880840C, 0x880850D, 0x880860E, 0x880870F, 0x8808800, 0x8808901, 0x8808A02, 0x8808B03, 0x8808C04, 0x8808D05, 0x8808E06, 0x8808F07};
//	int           nbits      = 28;

//  // GREE
  const int min_temp = 16;
  const int max_temp = 28;
  unsigned long ligar_cod  = 0x9270080A;
  unsigned long desli_cod  = 0x8270080A;
  unsigned long temp_cod[13] = {0x9200080A, 0x9280080A, 0x9240080A, 0x92C0080A, 0x9220080A, 0x92A0080A, 0x9260080A, 0x92E0080A, 0x9210080A, 0x9290080A, 0x9250080A, 0x92D0080A, 0x9230080A};
  int           nbits      = 32;
};

#endif
